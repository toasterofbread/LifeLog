package dev.toastbits.lifelog.core.accessor.impl

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructureProvider
import dev.toastbits.lifelog.core.accessor.DatabaseFilesParser
import dev.toastbits.lifelog.core.accessor.extension.DatabaseFileStructureExtension
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.walkFiles
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.SpecificationLogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.database.LogDataFile
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.impl.extension.ExtendableImpl
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path

class DatabaseFilesParserImpl(
    private val converter: LogFileConverter,
    private val strings: LogFileConverterStrings,
    private val fileStructureProvider: DatabaseFileStructureProvider,
    private val ioDispatcher: CoroutineDispatcher
): ExtendableImpl(), DatabaseFilesParser {
    override suspend fun parseDatabaseFileStructure(
        structure: FileStructure,
        onAlert: (ParseAlertData) -> Unit
    ): LogDatabase = withContext(ioDispatcher) {
        val days: MutableMap<LogDate, List<LogEvent>> = mutableMapOf()
        val data: MutableMap<LogEntityReference, LogDataFile> = mutableMapOf()

        val scope: Scope = Scope(days, data, onAlert)

        structure.preprocess(onAlert).walkFiles { file, path ->
            val reference: LogEntityReference =
                fileStructureProvider.parseReference(path.toString()) {
                    if (it is SpecificationLogParseAlert.UnknownReferenceType && it.firstUnknownSegment == 0) {
                        return@parseReference
                    }
                    onAlert(ParseAlertData(it, null, path.toString()))
                } ?: return@walkFiles

            when (reference) {
                is LogEntityReference.InLog -> {
                    scope.onInLogEntityReference(reference, file)
                }
                is LogEntityReference.InMetadata -> {
                    check(file is FileStructure.Node.File.FileLines)
                    scope.onInMetadataEntityReference(reference, file, path)
                }
                is LogEntityReference.URL -> throw IllegalStateException(reference.toString())
            }
        }

        return@withContext LogDatabase(days = scope.days, data = scope.data)
    }

    private suspend fun FileStructure.preprocess(onAlert: (ParseAlertData) -> Unit): FileStructure {
        var structure: FileStructure = this
        for (extension in extensions) {
            if (extension !is DatabaseFileStructureExtension) {
                continue
            }

            for (preprocessor in extension.extraPreprocessors) {
                structure = preprocessor.processDatabaseFileStructure(structure, fileStructureProvider, strings, extensions, onAlert)
            }
        }
        return structure
    }

    private suspend fun Scope.onInLogEntityReference(reference: LogEntityReference.InLog, file: FileStructure.Node.File) {
        if (reference.extensionId != null) {
            val dataFile: LogDataFile =
                when (file) {
                    is FileStructure.Node.File.FileLines -> LogDataFile.Lines(file.readLines().toList())
                    is FileStructure.Node.File.FileBytes -> file.readBytes().let { (bytes, range) -> LogDataFile.Bytes(bytes, range) }
                }

            data[reference] = dataFile
            return
        }

        when (reference.path.segments.lastOrNull()) {
            strings.logFileName -> {
                check(file is FileStructure.Node.File.FileLines)

                val log: LogFileConverter.ParseResult = converter.parseLogFile(file.readLines().asIterable())
                log.alerts.forEach(onAlert)

                for ((day, events) in log.days) {
                    val existingEvents: List<LogEvent> = days[day].orEmpty()
                    days[day] = existingEvents + events
                }
            }
            else -> TODO(reference.toString())
        }
    }

    private suspend fun Scope.onInMetadataEntityReference(reference: LogEntityReference.InMetadata, file: FileStructure.Node.File.FileLines, path: Path) {
        if (data.containsKey(reference)) {
            onAlert(ParseAlertData(SpecificationLogParseAlert.RedefinedMetadataValue(reference), null, path.toString()))
        }

        val extension: SpecificationExtension? = fileStructureProvider.findRegisteredExtension(reference.extensionId!!)
        if (extension == null) {
            onAlert(ParseAlertData(SpecificationLogParseAlert.UnregisteredExtension(reference.extensionId!!), null, path.toString()))
            return
        }

        val referenceType: LogEntityReferenceType.InMetadata? = extension.extraInMetadataReferenceTypes.firstOrNull { it.id == reference.referenceTypeId }
        if (referenceType == null) {
            onAlert(ParseAlertData(
                SpecificationLogParseAlert.UnregisteredReferenceType(
                    reference.referenceTypeId,
                    reference.extensionId
                ), null, path.toString()))
            return
        }

        val lines: Sequence<String> = file.readLines()
        val parsedMetadata: LogDataFile =
            referenceType.parseReferenceMetadata(reference.path.segments, lines) { onAlert(it.copy(filePath = path.toString())) }
            ?: return

        data[reference] = parsedMetadata
    }

    private class Scope(
        val days: MutableMap<LogDate, List<LogEvent>>,
        val data: MutableMap<LogEntityReference, LogDataFile>,
        val onAlert: (ParseAlertData) -> Unit
    )
}
