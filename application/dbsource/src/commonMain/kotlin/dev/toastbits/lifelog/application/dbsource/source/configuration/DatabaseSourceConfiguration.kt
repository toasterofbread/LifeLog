package dev.toastbits.lifelog.application.dbsource.source.configuration

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.source.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.source.type.InMemoryGitDatabaseSourceType
import kotlinx.serialization.Serializable

interface DatabaseSourceConfiguration {
    fun getType(): DatabaseSourceType<*>

    @Composable
    fun getPreviewTitle(): String

    @Composable
    fun getPreviewContent(): String
}

@Serializable
data class InMemoryGitDatabaseSourceConfiguration(
    val name: String,
    val repositoryUrl: String
): DatabaseSourceConfiguration {
    override fun getType(): DatabaseSourceType<InMemoryGitDatabaseSourceConfiguration> = InMemoryGitDatabaseSourceType

    @Composable
    override fun getPreviewTitle(): String =
        "$name (${getType().getName()})"

    @Composable
    override fun getPreviewContent(): String =
        "$repositoryUrl"
}
