package dev.toastbits.lifelog.application.dbsource.domain.accessor

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import org.jetbrains.compose.resources.StringResource

interface DatabaseAccessor {
    suspend fun loadOnlineDatabase(onProgress: (LoadProgress) -> Unit): Result<LogDatabaseParseResult>

    interface LoadProgress {
        val isError: Boolean get() = false

        fun getMessageResource(): StringResource

        @Composable
        fun getProgressMessage(): String? = null

        interface Absolute: LoadProgress {
            val progressFraction: Float
        }

        enum class Type {
            GENERIC,
            NETWORK;
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

fun DatabaseAccessor.LoadProgress.Companion.message(resource: StringResource): DatabaseAccessor.LoadProgress =
    object : DatabaseAccessor.LoadProgress {
        override fun getMessageResource(): StringResource = resource
    }
