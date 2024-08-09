package dev.toastbits.lifelog.core.saver.impl.github

import dev.toastbits.lifelog.core.saver.RemoteLogDatabaseSaver
import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

class GithubLogDatabaseSaver: RemoteLogDatabaseSaver {
    override fun canSaveDatabaseRemotely(database: LogDatabase): Boolean = true

    override suspend fun saveDatabaseRemotely(
        database: LogDatabase,
        message: String,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}
