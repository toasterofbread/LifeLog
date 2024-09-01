package dev.toastbits.lifelog.application.dbsource.inmemorygit.accessor

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor.LoadProgress
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import dev.toastbits.lifelog.application.dbsource.inmemorygit.configuration.InMemoryGitDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.inmemorygit.mapper.toLoadProgress
import dev.toastbits.lifelog.application.worker.WorkerClient
import dev.toastbits.lifelog.application.worker.command.WorkerCommandInMemoryGitClone
import dev.toastbits.lifelog.application.worker.executeCommand
import dev.toastbits.lifelog.application.worker.mapper.deserialise
import dev.toastbits.lifelog.application.worker.model.getOrThrow
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import dev.toastbits.lifelog.core.accessor.helper.LogDatabaseParseHelper
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.git.core.model.GitCredentials
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import kotlinx.coroutines.CoroutineDispatcher

// TODO | Local cache
class InMemoryGitDatabaseAccessor(
    private val configuration: InMemoryGitDatabaseSourceConfiguration,
    private val databaseConfigurationProvider: suspend () -> LogDatabaseConfiguration,
    private val gitCredentialsProvider: suspend () -> GitCredentials?,
    private val ioDispatcher: CoroutineDispatcher
): DatabaseAccessor {
    override val onlineLocationName: String
        @Composable
        get() = configuration.repositoryUrl

    override suspend fun loadOnlineDatabase(onProgress: (LoadProgress) -> Unit): Result<LogDatabaseParseResult> = runCatching {
        val databaseConfiguration: LogDatabaseConfiguration = databaseConfigurationProvider()
        val gitCredentials: GitCredentials? = gitCredentialsProvider()

        val command: WorkerCommandInMemoryGitClone = WorkerCommandInMemoryGitClone(configuration.repositoryUrl, configuration.branchName, gitCredentials)

        val fileStructureResult: WorkerCommandInMemoryGitClone.Response =
            WorkerClient.executeCommand<WorkerCommandInMemoryGitClone.Response, WorkerCommandInMemoryGitClone.Progress>(
                command,
                onProgress = { progress ->
                    onProgress(progress.stage.toLoadProgress(progress.part, progress.total))
                }
            ).getOrThrow().getOrThrow()

        val fileStructure: FileStructure = fileStructureResult.fileStructure.deserialise()

        val parser: LogDatabaseParseHelper = LogDatabaseParseHelper(databaseConfiguration, ioDispatcher)
        val alerts: MutableList<ParseAlertData> = mutableListOf()
        val database: LogDatabase = parser.parseFileStructure(fileStructure, alerts::add)

        return@runCatching LogDatabaseParseResult(database, alerts)
    }
}
