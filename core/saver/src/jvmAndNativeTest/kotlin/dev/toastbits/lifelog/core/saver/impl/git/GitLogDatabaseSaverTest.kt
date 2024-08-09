@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.toastbits.lifelog.core.saver.impl.git

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.toastbits.lifelog.core.git.GitWrapper
import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.saver.RemoteLogDatabaseSaver
import dev.toastbits.lifelog.core.saver.impl.DatabaseFilesGeneratorImpl
import dev.toastbits.lifelog.core.saver.impl.LogDatabaseFileStructureProviderImpl
import dev.toastbits.lifelog.core.saver.model.GitRemoteBranch
import dev.toastbits.lifelog.core.saver.reference.LogEntityReferenceGeneratorImpl
import dev.toastbits.lifelog.core.saver.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.test.FileSystemTest
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import kotlin.test.BeforeTest
import kotlin.test.Test

class GitLogDatabaseSaverTest: FileSystemTest {
    private lateinit var directory: Path
    private lateinit var repository: GitWrapper
    private lateinit var saver: RemoteLogDatabaseSaver

    override val fileSystem: FileSystem = FileSystem.SYSTEM

    private val eventText: LogEventType.EventText =
        LogEventType.EventText(
            prefix = "Prefix ",
            body = "Body",
            metadata = "Metadata"
        )
    private val mediaConsumeEventType: MediaConsumeEventType = mock {
        every { eventClass } returns MediaConsumeEvent::class
        every { generateEvent(any(), any(), any(), any()) } returns eventText
    }

//    private val mediaConsumeEventType: MediaConsumeEventType = MediaConsumeEventTypeImpl()
    private val mediaExtension: MediaExtension = MediaExtension(mediaConsumeEventType = mediaConsumeEventType)

    private val formats: LogFileConverterFormats = LogFileConverterImpl.DEFAULT_FORMATS
    private val splitStrategy: LogFileSplitStrategy = LogFileSplitStrategy.Month
    private val fileStructureProvider: LogDatabaseFileStructureProvider =
        LogDatabaseFileStructureProviderImpl(formats, splitStrategy).apply {
            registerExtension(mediaExtension)
        }

    private val referenceParser: LogEntityReferenceParser = LogEntityReferenceParserImpl(fileStructureProvider)
    private val logFileConverter: LogFileConverterImpl =
        LogFileConverterImpl(
            referenceParser,
            { LogEntityReferenceGeneratorImpl(fileStructureProvider, it) },
            formats
        ).apply { registerExtension(mediaExtension) }

    @BeforeTest
    fun setUp() {
        directory = getEmptyTempDir("lifelog-repo")

        repository = GitWrapper.create(directory, UnconfinedTestDispatcher())
        repository.setCredentials(SecretKeys.credentials)

        val remote: GitRemoteBranch = GitRemoteBranch("testRemote", "https://github.com/toasterofbread/test", "lifelog-test")

        saver = GitLogDatabaseSaver(repository, remote, DatabaseFilesGeneratorImpl(logFileConverter, fileStructureProvider, splitStrategy))
    }

    @Test
    fun testGitLogDatabaseSaver() = runTest {
        val content: UserContent = UserContent.single("Hello World!", setOf(UserContent.Modifier.Italic, UserContent.Modifier.Bold))
        val renderedContent: String = "***Hello World!***"

        val date: LocalDate = LocalDate.parse("2024-08-15")
        val reference: MediaReference = MovieOrShowMediaReference("test 2")
        val event: MovieOrShowMediaConsumeEvent =
            MovieOrShowMediaConsumeEvent(
                reference,
                iteration = 1,
                content = content
            )

        val database: LogDatabase =
            LogDatabase(
                days = mapOf(
                    LogDateImpl(date) to listOf(
                        event
                    )
                ),
                metadata = mapOf(
                    reference to LogEntityMetadata(20)
                )
            )

        val referenceGenerator: LogEntityReferenceGenerator = LogEntityReferenceGeneratorImpl(fileStructureProvider, date)
//        val referencePath: LogEntityPath = referenceGenerator.generateReferencePath(reference, relativeToOverride = LogEntityPath.ROOT) { assertThat(it).isNull() }!!

        saver.saveDatabaseRemotely(database, "Test ${formats.preferredDateFormat.format(date)}") { assertThat(it).isNull() }

        verify {
            mediaConsumeEventType.eventClass
            mediaConsumeEventType.generateEvent(event, any(), any(), any())
        }

        val logFile: Path = fileStructureProvider.getLogFilePath(date)
        val logFileContent: String =
            fileSystem.read(repository.directory.resolve(logFile)) {
                readUtf8()
            }

        val expectedFileContent: String = buildString {
            appendLine("${formats.datePrefix}${formats.preferredDateFormat.format(date)}")
            appendLine()
            appendLine("${eventText.prefix}${eventText.body} (${eventText.metadata}) {")
            appendLine()
            appendLine(renderedContent.prependIndent(formats.contentIndentation))
            appendLine()
            appendLine('}')
        }

        assertThat(logFileContent).isEqualTo(expectedFileContent)
    }
}
