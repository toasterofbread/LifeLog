package dev.toastbits.lifelog.application.dbsource.inmemorygit.type

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.composekit.settings.ui.component.item.mutablestate.MutableStateTextFieldSettingsItem
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.inmemorygit.accessor.InMemoryGitDatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.inmemorygit.configuration.InMemoryGitDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.worker.WorkerClient
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import dev.toastbits.lifelog.core.git.core.model.GitCredentials
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lifelog.application.dbsource.inmemorygit.generated.resources.Res
import lifelog.application.dbsource.inmemorygit.generated.resources.source_type_in_memory_git_description
import lifelog.application.dbsource.inmemorygit.generated.resources.source_type_in_memory_git_name
import org.jetbrains.compose.resources.stringResource

object InMemoryGitDatabaseSourceType: DatabaseSourceType<InMemoryGitDatabaseSourceConfiguration> {
    override fun isAvailableOnPlatform(): Boolean = true

    override fun createNewConfiguration(): InMemoryGitDatabaseSourceConfiguration =
        InMemoryGitDatabaseSourceConfiguration()

    override suspend fun onConfigurationDeleted(configuration: InMemoryGitDatabaseSourceConfiguration) {

    }

    override fun createAccessor(
        workerClient: WorkerClient,
        configuration: InMemoryGitDatabaseSourceConfiguration,
        databaseConfigurationProvider: suspend () -> LogDatabaseConfiguration,
        gitCredentialsProvider: suspend () -> GitCredentials?,
        httpClient: HttpClient,
        ioDispatcher: CoroutineDispatcher,
        workDispatcher: CoroutineDispatcher
    ): DatabaseAccessor =
        InMemoryGitDatabaseAccessor(workerClient, configuration, databaseConfigurationProvider, gitCredentialsProvider, ioDispatcher)

    override fun serialiseConfiguration(configuration: InMemoryGitDatabaseSourceConfiguration): String =
        Json.encodeToString(configuration)

    override fun deserialiseConfiguration(serialisedConfiguration: String): InMemoryGitDatabaseSourceConfiguration =
        Json.decodeFromString(serialisedConfiguration)

    @Composable
    override fun getName(): String = stringResource(Res.string.source_type_in_memory_git_name)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.source_type_in_memory_git_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.Cloud

    override fun getLazyListConfigurationItems(
        configuration: InMemoryGitDatabaseSourceConfiguration,
        onChange: (InMemoryGitDatabaseSourceConfiguration) -> Unit
    ): List<SettingsItem> =
        listOf(
            MutableStateTextFieldSettingsItem(
                value = configuration.name,
                onSet = { onChange(configuration.copy(name = it)) },
                getPropertyName = { "name" },
                getPropertyDescription = { null }
            ),
            MutableStateTextFieldSettingsItem(
                value = configuration.repositoryUrl,
                onSet = { onChange(configuration.copy(repositoryUrl = it)) },
                getPropertyName = { "repositoryUrl" },
                getPropertyDescription = { null }
            ),
            MutableStateTextFieldSettingsItem(
                value = configuration.branchName,
                onSet = { onChange(configuration.copy(branchName = it)) },
                getPropertyName = { "branchName" },
                getPropertyDescription = { null }
            )
        )
    }
