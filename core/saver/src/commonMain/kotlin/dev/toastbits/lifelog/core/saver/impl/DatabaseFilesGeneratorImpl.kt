package dev.toastbits.lifelog.core.saver.impl

import dev.toastbits.lifelog.core.saver.DatabaseFileStructure
import dev.toastbits.lifelog.core.saver.DatabaseFilesGenerator
import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.saver.MutableDatabaseFileStructure
import dev.toastbits.lifelog.core.saver.splitDaysIntoGroups
import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import okio.Path

class DatabaseFilesGeneratorImpl(
    private val converter: LogFileConverter,
    private val fileStructureProvider: LogDatabaseFileStructureProvider,
    private val splitStrategy: LogFileSplitStrategy,
): DatabaseFilesGenerator {
    override fun generateDatabaseFileStructure(
        database: LogDatabase,
        onAlert: (GenerateAlertData) -> Unit
    ): DatabaseFileStructure {
        val structure: MutableDatabaseFileStructure = MutableDatabaseFileStructure()

        structure.writeDays(database.days, onAlert)
        structure.writeMetadata(database.metadata, onAlert)

        return structure
    }

    private fun MutableDatabaseFileStructure.writeDays(
        days: Map<LogDate, List<LogEvent>>,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        val dayGroups: List<List<LogDate>> = splitStrategy.splitDaysIntoGroups(days.keys)
        for (group in dayGroups) {
            val generateResult: LogFileConverter.GenerateResult =
                converter.generateLogFile(group.associateWith { days[it]!! })
            generateResult.alerts.forEach(onAlert)

            val filePath: Path = fileStructureProvider.getLogFilePath(group.first().date)
            createFile(filePath, generateResult.lines)
        }
    }

    private fun MutableDatabaseFileStructure.writeMetadata(
        metadataEntries: Map<LogEntityReference, LogEntityMetadata>,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        for ((reference, metadata) in metadataEntries) {
            val filePath: Path = fileStructureProvider.getEntityReferenceFilePath(reference)
            createFile(filePath, listOf("TEMP"))
        }
    }
}
