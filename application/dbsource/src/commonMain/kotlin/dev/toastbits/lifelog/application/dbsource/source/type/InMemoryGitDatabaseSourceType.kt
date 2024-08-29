package dev.toastbits.lifelog.application.dbsource.source.type

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.lifelog.application.dbsource.source.configuration.InMemoryGitDatabaseSourceConfiguration
import lifelog.application.dbsource.generated.resources.Res
import lifelog.application.dbsource.generated.resources.database_source_type_in_memory_git_description
import lifelog.application.dbsource.generated.resources.database_source_type_in_memory_git_name
import org.jetbrains.compose.resources.stringResource

object InMemoryGitDatabaseSourceType: DatabaseSourceType<InMemoryGitDatabaseSourceConfiguration> {
    override fun isAvailableOnPlatform(): Boolean = true

    @Composable
    override fun getName(): String = stringResource(Res.string.database_source_type_in_memory_git_name)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.database_source_type_in_memory_git_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.Cloud

    @Composable
    override fun ColumnScope.ConfigurationItems(
        modifier: Modifier,
        configuration: InMemoryGitDatabaseSourceConfiguration,
        onChange: (InMemoryGitDatabaseSourceConfiguration) -> Unit
    ) {
        Text("Config item 1")
        Text("Config item 2")
        Text("Config item 3")
    }
}
