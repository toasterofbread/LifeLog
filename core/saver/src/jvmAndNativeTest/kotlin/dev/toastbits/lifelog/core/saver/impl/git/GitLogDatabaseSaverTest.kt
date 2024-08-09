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
import dev.toastbits.lifelog.core.saver.impl.LogDatabaseFileStructureProviderImpl
import dev.toastbits.lifelog.core.saver.model.GitRemoteBranch
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.test.FileSystemTest
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.model.reference.MovieOrShowMediaReference
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.media.model.entity.event.MovieOrShowMediaConsumeEvent
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
    private val splitStrategy: LogFileSplitStrategy = LogFileSplitStrategy.Month
    private val fileStructureProvider: LogDatabaseFileStructureProvider = LogDatabaseFileStructureProviderImpl(splitStrategy)

    private val eventText: LogEventType.EventText =
        LogEventType.EventText(
            prefix = "Prefix",
            body = "Body",
            metadata = "Metadata"
        )
    private val mediaConsumeEventType: MediaConsumeEventType = mock {
        every { canGenerateEvent(any()) } calls { it.args.first() is MediaConsumeEvent }
        every { generateEvent(any(), any(), any(), any()) } returns eventText
    }
    private val mediaExtension: MediaExtension = MediaExtension(mediaConsumeEventType = mediaConsumeEventType)

    private val formats: LogFileConverterFormats = LogFileConverterImpl.DEFAULT_FORMATS
    private val logFileConverter: LogFileConverterImpl = LogFileConverterImpl(formats).apply { registerExtension(mediaExtension) }

    @BeforeTest
    fun setUp() {
        directory = getEmptyTempDir("lifelog-repo")

        repository = GitWrapper.create(directory, UnconfinedTestDispatcher())
        repository.setCredentials(SecretKeys.credentials)

        val remote: GitRemoteBranch = GitRemoteBranch("testRemote", "https://github.com/toasterofbread/test", "lifelog-test")

        saver = GitLogDatabaseSaver(repository, remote, logFileConverter, fileStructureProvider, splitStrategy)
    }

    @Test
    fun testGitLogDatabaseSaver() = runTest {
        val date: LocalDate = LocalDate.parse("2024-08-14")
        val event: MovieOrShowMediaConsumeEvent = MovieOrShowMediaConsumeEvent(MovieOrShowMediaReference("test 2"), iteration = 1)

        val database: LogDatabase =
            LogDatabase(
                days = mapOf(
                    LogDateImpl(date) to listOf(
                        event
                    )
                )
            )

        saver.saveDatabaseRemotely(database, "Test ${formats.preferredDateFormat.format(date)}") { assertThat(it).isNull() }

        verify {
            mediaConsumeEventType.canGenerateEvent(event)
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
            appendLine("${eventText.prefix}${eventText.body} (${eventText.metadata})")
        }

        assertThat(logFileContent).isEqualTo(expectedFileContent)
    }
}
