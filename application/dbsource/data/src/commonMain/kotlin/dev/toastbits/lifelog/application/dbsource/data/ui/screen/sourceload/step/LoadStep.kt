package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult

internal sealed interface LoadStep {
    @Composable
    fun getStatusText(accessor: DatabaseAccessor): String

    suspend fun execute(accessor: DatabaseAccessor, onProgress: (DatabaseAccessor.LoadProgress?) -> Unit): ExecuteResult

    sealed interface ExecuteResult {
        data class ExceptionThrown(val exception: Throwable): ExecuteResult
        data class DatabaseLoaded(val parseResult: LogDatabaseParseResult): ExecuteResult
        data class NextStep(val nextStep: LoadStep): ExecuteResult
    }
}
