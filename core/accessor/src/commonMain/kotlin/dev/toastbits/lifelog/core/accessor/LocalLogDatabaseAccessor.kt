package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

interface LocalLogDatabaseAccessor {
    suspend fun saveDatabaseLocally(database: LogDatabase, onAlert: (GenerateAlertData) -> Unit)
    suspend fun loadDatabaseLocally(onAlert: (ParseAlertData) -> Unit): LogDatabase
}
