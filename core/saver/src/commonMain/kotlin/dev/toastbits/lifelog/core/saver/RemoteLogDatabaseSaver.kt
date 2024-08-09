package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

interface RemoteLogDatabaseSaver {
    fun canSaveDatabaseRemotely(database: LogDatabase): Boolean
    suspend fun saveDatabaseRemotely(database: LogDatabase, message: String, onAlert: (GenerateAlertData) -> Unit)
}
