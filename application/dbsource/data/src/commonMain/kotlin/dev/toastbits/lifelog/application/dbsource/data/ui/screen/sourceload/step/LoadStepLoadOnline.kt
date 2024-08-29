package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.`database_loader_loading_online_database_at_$location`
import org.jetbrains.compose.resources.stringResource

internal data object LoadStepLoadOnline: LoadStep {
    @Composable
    override fun getStatusText(accessor: DatabaseAccessor): String =
        stringResource(Res.string.`database_loader_loading_online_database_at_$location`).replace("\$location", accessor.onlineLocationName)

    override suspend fun execute(accessor: DatabaseAccessor, onProgress: (DatabaseAccessor.LoadProgress?) -> Unit): LoadStep.ExecuteResult =
        accessor.loadOnlineDatabase(onProgress)
            .fold(
                onSuccess = {
                    LoadStep.ExecuteResult.DatabaseLoaded(it)
                },
                onFailure = {
                    LoadStep.ExecuteResult.ExceptionThrown(it)
                }
            )
}
