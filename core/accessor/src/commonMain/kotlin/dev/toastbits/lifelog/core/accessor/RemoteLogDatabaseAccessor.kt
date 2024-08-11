package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

interface RemoteLogDatabaseAccessor {
    fun canSaveDatabaseRemotely(database: LogDatabase): Boolean
    suspend fun saveDatabaseRemotely(database: LogDatabase, message: String, onAlert: (GenerateAlertData) -> Unit)

    fun canLoadDatabaseRemotely(): Boolean
    suspend fun loadDatabaseRemotely(onAlert: (ParseAlertData) -> Unit): LogDatabase
}
