package dev.toastbits.lifelog.core.accessor.impl.github

import dev.toastbits.lifelog.core.accessor.RemoteLogDatabaseAccessor
import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

class GithubLogDatabaseAccessor: RemoteLogDatabaseAccessor {
    override fun canSaveDatabaseRemotely(database: LogDatabase): Boolean = true

    override suspend fun saveDatabaseRemotely(
        database: LogDatabase,
        message: String,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun canLoadDatabaseRemotely(): Boolean = true

    override suspend fun loadDatabaseRemotely(onAlert: (ParseAlertData) -> Unit): LogDatabase {
        TODO("Not yet implemented")
    }
}
