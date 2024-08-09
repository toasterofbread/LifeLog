package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

interface LocalLogDatabaseSaver {
    suspend fun saveDatabaseLocally(database: LogDatabase, onAlert: (GenerateAlertData) -> Unit)
}
