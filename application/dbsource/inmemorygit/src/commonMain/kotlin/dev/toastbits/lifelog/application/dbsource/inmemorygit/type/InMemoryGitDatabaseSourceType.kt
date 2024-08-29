package dev.toastbits.lifelog.application.dbsource.inmemorygit.type

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.inmemorygit.accessor.InMemoryGitDatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.inmemorygit.configuration.InMemoryGitDatabaseSourceConfiguration
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lifelog.application.dbsource.domain.generated.resources.Res
import lifelog.application.dbsource.domain.generated.resources.database_source_type_in_memory_git_description
import lifelog.application.dbsource.domain.generated.resources.database_source_type_in_memory_git_name
import org.jetbrains.compose.resources.stringResource

object InMemoryGitDatabaseSourceType: DatabaseSourceType<InMemoryGitDatabaseSourceConfiguration> {
    override fun isAvailableOnPlatform(): Boolean = true

    override fun createNewConfiguration(): InMemoryGitDatabaseSourceConfiguration =
        InMemoryGitDatabaseSourceConfiguration()

    override fun createAccessor(
        configuration: InMemoryGitDatabaseSourceConfiguration,
        databaseConfiguration: LogDatabaseConfiguration,
        httpClient: HttpClient,
        ioDispatcher: CoroutineDispatcher
    ): DatabaseAccessor =
        InMemoryGitDatabaseAccessor(configuration, databaseConfiguration, httpClient, ioDispatcher)

    override fun serialiseConfiguration(configuration: InMemoryGitDatabaseSourceConfiguration): String =
        Json.encodeToString(configuration)

    override fun deserialiseConfiguration(serialisedConfiguration: String): InMemoryGitDatabaseSourceConfiguration =
        Json.decodeFromString(serialisedConfiguration)

    @Composable
    override fun getName(): String = stringResource(Res.string.database_source_type_in_memory_git_name)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.database_source_type_in_memory_git_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.Cloud

    override fun LazyListScope.lazyListConfigurationItems(
        configuration: InMemoryGitDatabaseSourceConfiguration,
        modifier: Modifier,
        onChange: (InMemoryGitDatabaseSourceConfiguration) -> Unit
    ) {
        item {
            Text("Config item 1")
        }
        item {
            Text("Config item 2")
        }
        item {
            Text("Config item 3")
        }
    }
}
