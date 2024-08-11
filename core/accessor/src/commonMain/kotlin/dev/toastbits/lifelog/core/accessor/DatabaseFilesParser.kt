package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import okio.FileSystem

interface DatabaseFilesParser {
    suspend fun parseDatabaseFileStructure(
        structure: DatabaseFileStructure,
        fileSystem: FileSystem,
        onAlert: (ParseAlertData) -> Unit
    ): LogDatabase
}
