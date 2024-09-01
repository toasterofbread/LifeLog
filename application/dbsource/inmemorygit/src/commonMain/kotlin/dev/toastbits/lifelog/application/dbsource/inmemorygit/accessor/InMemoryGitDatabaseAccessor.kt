package dev.toastbits.lifelog.application.dbsource.inmemorygit.accessor

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor.LoadProgress
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import dev.toastbits.lifelog.application.dbsource.inmemorygit.configuration.InMemoryGitDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.inmemorygit.mapper.toLoadProgress
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import dev.toastbits.lifelog.core.accessor.helper.LogDatabaseParseHelper
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.git.core.model.GitCredentials
import dev.toastbits.lifelog.core.git.memory.helper.GitHelper
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher

// TODO | Local cache
class InMemoryGitDatabaseAccessor(
    private val configuration: InMemoryGitDatabaseSourceConfiguration,
    private val databaseConfiguration: LogDatabaseConfiguration,
    private val gitCredentialsProvider: suspend () -> GitCredentials?,
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val workDispatcher: CoroutineDispatcher
): DatabaseAccessor {
    override val onlineLocationName: String
        @Composable
        get() = configuration.repositoryUrl

    override suspend fun loadOnlineDatabase(onProgress: (LoadProgress) -> Unit): Result<LogDatabaseParseResult> = runCatching {
        val gitCredentials: GitCredentials? = gitCredentialsProvider()
        val gitHelper: GitHelper = GitHelper(httpClient, ioDispatcher, workDispatcher, gitCredentials)

        val fileStructure: FileStructure =
            gitHelper.cloneToFileStructure(configuration.repositoryUrl, configuration.branchName) { stage, part, total ->
                onProgress(stage.toLoadProgress(part, total))
            }.getOrThrow()

        val parser: LogDatabaseParseHelper = LogDatabaseParseHelper(databaseConfiguration, ioDispatcher)
        val alerts: MutableList<ParseAlertData> = mutableListOf()
        val database: LogDatabase = parser.parseFileStructure(fileStructure, alerts::add)

        return@runCatching LogDatabaseParseResult(database, alerts)
    }
}
