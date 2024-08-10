package dev.toastbits.lifelog.core.saver.impl

import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.extension.validate
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import kotlinx.datetime.LocalDate
import okio.Path
import okio.Path.Companion.toPath

class LogDatabaseFileStructureProviderImpl(
    private val strings: LogFileConverterStrings,
    private val splitStrategy: LogFileSplitStrategy
): LogDatabaseFileStructureProvider {
    private val extensions: MutableList<SpecificationExtension> = mutableListOf()

    init {
        strings.validate()
    }

    override fun registerExtension(extension: SpecificationExtension) {
        extension.validate()
        extensions.add(extension)
    }

    override fun unregisterExtension(extension: SpecificationExtension) {
        extensions.remove(extension)
    }

    override fun getLogFilePath(date: LocalDate): Path =
        (getLogFileDirectory(date) + listOf(strings.logFileName)).toPath()

    override fun getEntityReferenceFilePath(reference: LogEntityReference): Path {
        when (reference) {
            is LogEntityReference.InLog -> {
                return (getLogFileDirectory(reference.logDate) + reference.path.segments).toPath()
            }
            is LogEntityReference.InMetadata -> {
                for (extension in extensions) {
                    val referenceType: LogEntityReferenceType =
                        extension.extraReferenceTypes.firstOrNull { it.extensionId == reference.extensionId } ?: continue

                    return (
                        listOf(
                            strings.metadataDirectoryName,
                            strings.metadataExtensionDirectoryName,
                            extension.name,
                            referenceType.identifier
                        ) + reference.path.segments
                    ).toPath()
                }

                throw IllegalStateException("Could not find extension providing reference type for $this")
            }
        }
    }

    override fun parseReference(
        text: String,
        onAlert: (LogParseAlert) -> Unit
    ): LogEntityReference? {
        val normalisedPath: List<String> = text.toPath().normalized().segments

        if (normalisedPath.any { it == ".." }) {
            onAlert(LogParseAlert.InvalidReferenceFormat(text))
            return null
        }

        when (normalisedPath.firstOrNull()) {
            strings.metadataDirectoryName -> {
                return parseMetadataReference(normalisedPath.drop(1), onAlert) {
                    onAlert(LogParseAlert.UnknownReferenceType(normalisedPath, it + 1))
                }
            }
            strings.logsDirectoryName -> {
                return parseLogsReference(normalisedPath.drop(1), onAlert) {
                    onAlert(LogParseAlert.UnknownReferenceType(normalisedPath, it + 1))
                }
            }
            else -> {
                onAlert(LogParseAlert.UnknownReferenceType(normalisedPath, 0))
                return null
            }
        }
    }

    private fun parseMetadataReference(path: List<String>, onAlert: (LogParseAlert) -> Unit, onFailure: (Int) -> Unit): LogEntityReference? {
        when (path.firstOrNull()) {
            strings.metadataExtensionDirectoryName -> {
                return parseExtensionMetadataReference(path.drop(1), onAlert) { onFailure(it + 1) }
            }
            else -> {
                onFailure(0)
                return null
            }
        }
    }

    private fun parseExtensionMetadataReference(path: List<String>, onAlert: (LogParseAlert) -> Unit, onFailure: (Int) -> Unit): LogEntityReference? {
        val extension: SpecificationExtension? = extensions.firstOrNull { it.name == path.getOrNull(0) }
        if (extension == null) {
            onFailure(0)
            return null
        }

        val referenceType: LogEntityReferenceType? = extension.extraReferenceTypes.firstOrNull { it.identifier == path.getOrNull(1) }
        if (referenceType == null) {
            onFailure(1)
            return null
        }

        return referenceType.parseReference(path.drop(2)) { alert ->
            if (alert is LogParseAlert.UnknownReferenceType) {
                onFailure(alert.firstUnknownSegment + 2)
            }
            else {
                onAlert(alert)
            }
        }
    }

    private fun parseLogsReference(path: List<String>, onAlert: (LogParseAlert) -> Unit, onFailure: (Int) -> Unit): LogEntityReference? {
        if (path.size < splitStrategy.componentsCount) {
            onFailure(0)
            return null
        }

        val dateParts: List<Int> =
            path.take(splitStrategy.componentsCount).mapIndexed { index, part ->
                val int: Int? = part.toIntOrNull()
                if (int == null) {
                    onFailure(index)
                    return null
                }
                return@mapIndexed int
            }

        val date: LocalDate = splitStrategy.parseDateComponents(dateParts)
        return LogEntityReference.InLogData(date, LogEntityPath(path.drop(splitStrategy.componentsCount)))
    }

    private fun getLogFileDirectory(date: LocalDate): List<String> =
        listOf(strings.logsDirectoryName) + splitStrategy.getDateComponents(date).map { it.toString().padStart(2, '0') }
}

fun List<String>.toPath(): Path =
    joinToString(Path.DIRECTORY_SEPARATOR).toPath()
