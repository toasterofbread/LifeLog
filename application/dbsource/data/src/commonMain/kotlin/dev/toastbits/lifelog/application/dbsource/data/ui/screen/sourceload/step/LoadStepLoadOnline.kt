package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step

import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor

internal data object LoadStepLoadOnline: LoadStep {
    override suspend fun execute(accessor: DatabaseAccessor, onProgress: (DatabaseAccessor.LoadProgress) -> Unit): LoadStep.ExecuteResult =
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
