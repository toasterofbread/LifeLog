package dev.toastbits.lifelog.core.accessor.impl

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.DatabaseFilesGenerator
import dev.toastbits.lifelog.core.accessor.DatabaseFileStructureProvider
import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.accessor.MutableDatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.splitDaysIntoGroups
import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.database.LogDataFile
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import okio.Path

class DatabaseFilesGeneratorImpl(
    private val converter: LogFileConverter,
    private val fileStructureProvider: DatabaseFileStructureProvider,
    private val splitStrategy: LogFileSplitStrategy,
): DatabaseFilesGenerator {
    override fun generateDatabaseFileStructure(
        database: LogDatabase,
        onAlert: (GenerateAlertData) -> Unit
    ): DatabaseFileStructure {
        val structure: MutableDatabaseFileStructure = MutableDatabaseFileStructure()

        structure.writeDays(database.days, onAlert)
        structure.writeDataFiles(database.data, onAlert)

        return structure
    }

    private fun MutableDatabaseFileStructure.writeDays(
        days: Map<LogDate, List<LogEvent>>,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        val dayGroups: List<List<LogDate>> = splitStrategy.splitDaysIntoGroups(days.keys)
        for (group in dayGroups) {
            val filePath: Path = fileStructureProvider.getLogFilePath(group.first().date)
            val generateResult: LogFileConverter.GenerateResult =
                converter.generateLogFile(group.associateWith { days[it]!! })

            for (alert in generateResult.alerts) {
                onAlert(alert.copy(filePath = filePath.toString()))
            }

            createFile(filePath, generateResult.lines)
        }
    }

    private fun MutableDatabaseFileStructure.writeDataFiles(
        dataEntries: Map<LogEntityReference, LogDataFile>,
        onAlert: (GenerateAlertData) -> Unit
    ) {
        for ((reference, data) in dataEntries) {
            val filePath: Path = fileStructureProvider.getEntityReferenceFilePath(reference)
            createFile(
                filePath,
                when (data) {
                    is LogDataFile.Lines -> DatabaseFileStructure.Node.FileLinesData(data.lines)
                    is LogDataFile.Bytes -> DatabaseFileStructure.Node.FileBytesData(data.bytes)
                }
            )
        }
    }
}
