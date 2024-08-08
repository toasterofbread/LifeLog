package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.database.LogDatabase

interface LogDatabaseSaver {
    suspend fun saveDatabase(database: LogDatabase)
}
