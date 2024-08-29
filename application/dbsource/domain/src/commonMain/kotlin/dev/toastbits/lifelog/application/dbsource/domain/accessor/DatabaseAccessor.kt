package dev.toastbits.lifelog.application.dbsource.domain.accessor

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.utils.common.roundTo
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor.LoadProgress
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor.LoadProgress.Absolute
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import lifelog.application.dbsource.domain.generated.resources.Res
import lifelog.application.dbsource.domain.generated.resources.`database_accessor_load_progress_network_$bytes`
import lifelog.application.dbsource.domain.generated.resources.`database_accessor_load_progress_network_$bytes_of_$total_$percent`
import org.jetbrains.compose.resources.stringResource

interface DatabaseAccessor {
    val onlineLocationName: String
        @Composable get

    suspend fun loadOnlineDatabase(onProgress: (LoadProgress) -> Unit): Result<LogDatabaseParseResult>

    fun interface LoadProgress {
        @Composable
        fun getMessage(): String

        @Composable
        fun getProgressMessage(): String? = null

        interface Absolute: LoadProgress {
            val progressFraction: Float
        }

        companion object
    }
}

interface OfflineDatabaseAccessor: DatabaseAccessor {
    val offlineLocationName: String
        @Composable get

    suspend fun checkIfUpToDate(): Result<Boolean>
    suspend fun loadOfflineDatabase(): Result<LogDatabaseParseResult>
}

fun DatabaseAccessor.LoadProgress.Companion.Network(
    processedBytes: Long?,
    totalBytes: Long?,
    getMessage: @Composable () -> String
): LoadProgress =
    if (processedBytes != null && totalBytes != null)
        object: Absolute {
            override val progressFraction: Float = processedBytes.toFloat() / totalBytes

            @Composable
            override fun getMessage(): String = getMessage()

            @Composable
            override fun getProgressMessage(): String =
                stringResource(Res.string.`database_accessor_load_progress_network_$bytes_of_$total_$percent`)
                    .replace("\$bytes", processedBytes.toString())
                    .replace("\$total", totalBytes.toString())
                    .replace("\$percent", (progressFraction * 100).roundTo(2).toString())
        }
    else
        object : LoadProgress {
            @Composable
            override fun getMessage(): String = getMessage()

            @Composable
            override fun getProgressMessage(): String? =
                if (processedBytes == null) null
                else stringResource(Res.string.`database_accessor_load_progress_network_$bytes`).replace("\$bytes", processedBytes.toString())
        }
