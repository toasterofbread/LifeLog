package dev.toastbits.lifelog.application.dbsource.inmemorygit.configuration

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.inmemorygit.type.InMemoryGitDatabaseSourceType
import kotlinx.serialization.Serializable
import lifelog.application.dbsource.inmemorygit.generated.resources.Res
import lifelog.application.dbsource.inmemorygit.generated.resources.source_configuration_repository_url_not_http
import lifelog.application.dbsource.inmemorygit.generated.resources.source_configuration_repository_url_not_set
import lifelog.application.dbsource.inmemorygit.generated.resources.source_configuration_branch_not_set
import org.jetbrains.compose.resources.stringResource

@Serializable
data class InMemoryGitDatabaseSourceConfiguration(
    val name: String = "",
    val repositoryUrl: String = "",
    val branchName: String = ""
): DatabaseSourceConfiguration {
    override fun getType(): DatabaseSourceType<InMemoryGitDatabaseSourceConfiguration> = InMemoryGitDatabaseSourceType

    @Composable
    override fun getPreviewTitle(): String =
        "$name (${getType().getName()})"

    @Composable
    override fun getPreviewContent(): String =
        "$repositoryUrl" // TODO

    @Composable
    override fun getInvalidReasonMessage(): String? {
        if (repositoryUrl.isBlank()) {
            return stringResource(Res.string.source_configuration_repository_url_not_set)
        }
        if (!repositoryUrl.startsWith("http://") && !repositoryUrl.startsWith("https://")) {
            return stringResource(Res.string.source_configuration_repository_url_not_http)
        }
        if (branchName.isBlank()) {
            return stringResource(Res.string.source_configuration_branch_not_set)
        }
        return null
    }
}
