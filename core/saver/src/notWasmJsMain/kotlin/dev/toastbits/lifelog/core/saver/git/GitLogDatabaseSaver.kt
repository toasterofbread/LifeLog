package dev.toastbits.lifelog.core.saver.git

import dev.toastbits.lifelog.core.git.GitWrapper
import dev.toastbits.lifelog.core.saver.LogDatabaseSaver
import dev.toastbits.lifelog.core.specification.database.LogDatabase

class GitLogDatabaseSaver(
    private val repository: GitWrapper
): LogDatabaseSaver {
    override suspend fun saveDatabase(database: LogDatabase) {
    }
}
