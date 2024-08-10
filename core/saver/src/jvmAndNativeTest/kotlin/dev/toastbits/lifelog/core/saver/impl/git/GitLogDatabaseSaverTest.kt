@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.toastbits.lifelog.core.saver.impl.git

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.toastbits.lifelog.core.git.GitWrapper
import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.saver.RemoteLogDatabaseSaver
import dev.toastbits.lifelog.core.saver.impl.DatabaseFilesGeneratorImpl
import dev.toastbits.lifelog.core.saver.impl.LogDatabaseFileStructureProviderImpl
import dev.toastbits.lifelog.core.saver.model.GitRemoteBranch
import dev.toastbits.lifelog.core.saver.reference.LogEntityReferenceGeneratorImpl
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterImpl
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.test.FileSystemTest
import dev.toastbits.lifelog.core.test.extension.TestLogEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import kotlin.test.BeforeTest
import kotlin.test.Test
import dev.toastbits.lifelog.core.test.extension.TestExtension

class GitLogDatabaseSaverTest: FileSystemTest {
    private lateinit var directory: Path
    private lateinit var repository: GitWrapper
    private lateinit var saver: RemoteLogDatabaseSaver

    override val fileSystem: FileSystem = FileSystem.SYSTEM

    private val formats: LogFileConverterStrings = LogFileConverterImpl.DEFAULT_FORMATS
    private val splitStrategy: LogFileSplitStrategy = LogFileSplitStrategy.Month
    private val fileStructureProvider: LogDatabaseFileStructureProvider =
        LogDatabaseFileStructureProviderImpl(formats, splitStrategy).apply {
            registerExtension(TestExtension)
        }

    private val referenceParser: LogEntityReferenceParser = fileStructureProvider
    private val logFileConverter: LogFileConverterImpl =
        LogFileConverterImpl(
            referenceParser,
            { LogEntityReferenceGeneratorImpl(fileStructureProvider, it) },
            formats
        ).apply { registerExtension(TestExtension) }

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
        val reference: LogEntityReference.InMetadata =
            LogEntityReference.InMetadataData(
                LogEntityPath.of("test 2"),
                TestExtension.id
            )
        val event: LogEvent = TestLogEvent(reference, content = content)

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

        saver.saveDatabaseRemotely(database, "Test ${formats.preferredDateFormat.format(date)}") { assertThat(it).isNull() }

        val logFile: Path = fileStructureProvider.getLogFilePath(date)
        val logFileContent: String =
            fileSystem.read(repository.directory.resolve(logFile)) {
                readUtf8()
            }

        val referenceGenerator: LogEntityReferenceGenerator = LogEntityReferenceGeneratorImpl(fileStructureProvider, date)
        val referencePath: LogEntityPath = referenceGenerator.generateReferencePath(reference) { assertThat(it).isNull() }!!

        assertThat(logFileContent).contains(formats.datePrefix)
        assertThat(logFileContent).contains(formats.preferredDateFormat.format(date))
        assertThat(logFileContent).contains(renderedContent.prependIndent(formats.contentIndentation))
        assertThat(logFileContent).contains(referencePath.toString())

        val metadataFile: Path = fileStructureProvider.getEntityReferenceFilePath(reference)
        val metadataFileContent: String =
            fileSystem.read(repository.directory.resolve(metadataFile)) {
                readUtf8()
            }

        val expectedMetadataFileContent: String =
            buildString {
                append("TEMP")
            }
        assertThat(metadataFileContent).isEqualTo(expectedMetadataFileContent)
    }
}
