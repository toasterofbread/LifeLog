package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step.LoadStep.ExecuteResult
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.accessor.OfflineDatabaseAccessor
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.`database_loader_loading_offline_database_at_$location`
import org.jetbrains.compose.resources.stringResource

internal data class LoadStepLoadOffline(val accessor: OfflineDatabaseAccessor): LoadStep {
    @Composable
    override fun getStatusText(accessor: DatabaseAccessor): String =
        stringResource(Res.string.`database_loader_loading_offline_database_at_$location`).replace("\$location", this.accessor.offlineLocationName)

    override suspend fun execute(
        accessor: DatabaseAccessor,
        onProgress: (DatabaseAccessor.LoadProgress?) -> Unit
    ): ExecuteResult {
        TODO("Not yet implemented")
    }
}
