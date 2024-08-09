package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.database.LogDatabase

interface DatabaseFilesGenerator {
    fun generateDatabaseFileStructure(database: LogDatabase, onAlert: (GenerateAlertData) -> Unit): DatabaseFileStructure
}
