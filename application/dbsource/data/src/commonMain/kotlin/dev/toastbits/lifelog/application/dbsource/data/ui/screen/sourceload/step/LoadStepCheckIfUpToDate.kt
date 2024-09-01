package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step

import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step.LoadStep.ExecuteResult
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.accessor.OfflineDatabaseAccessor

internal data class LoadStepCheckIfUpToDate(val accessor: OfflineDatabaseAccessor): LoadStep {
    override suspend fun execute(
        accessor: DatabaseAccessor,
        onProgress: (DatabaseAccessor.LoadProgress) -> Unit
    ): ExecuteResult {
        TODO("Not yet implemented")
    }
}
