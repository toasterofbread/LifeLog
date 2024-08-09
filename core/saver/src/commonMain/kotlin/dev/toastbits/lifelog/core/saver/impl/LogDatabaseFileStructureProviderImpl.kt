package dev.toastbits.lifelog.core.saver.impl

import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.extension.validate
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import kotlinx.datetime.LocalDate
import okio.Path
import okio.Path.Companion.toPath

class LogDatabaseFileStructureProviderImpl(
    private val formats: LogFileConverterFormats,
    private val splitStrategy: LogFileSplitStrategy,
    private val logFileName: String = "log.md",
    private val logsDirectoryName: String = "logs",
    private val logImagesDirectoryName: String = "images",
): LogDatabaseFileStructureProvider {
    private val extensions: MutableList<SpecificationExtension> = mutableListOf()

    init {
        formats.validate()
    }

    override fun registerExtension(extension: SpecificationExtension) {
        extension.validate()
        extensions.add(extension)
    }

    override fun unregisterExtension(extension: SpecificationExtension) {
        extensions.remove(extension)
    }

    override fun getLogFilePath(date: LocalDate): Path =
        (getLogFileDirectory(date) + listOf(logFileName)).toPath()

    override fun getLogImagesPath(date: LocalDate): Path =
        (getLogFileDirectory(date) + listOf(logImagesDirectoryName)).toPath()

    override fun getEntityReferencePath(reference: LogEntityReference): Path {
        for (extension in extensions) {
            val type: LogEntityReferenceType =
                extension.extraReferenceTypes.firstOrNull { it.referenceClass.isInstance(reference) } ?: continue

            return (
                listOf(
                    formats.metadataDirectoryName,
                    formats.extensionDirectoryName,
                    extension.identifier,
                    type.identifier
                ) + reference.entityPath.segments
            ).toPath()
        }

        throw IllegalStateException("Could not find extension providing reference type for $reference (${reference::class})")
    }

    private fun getLogFileDirectory(date: LocalDate): List<String> =
        listOf(logsDirectoryName) + splitStrategy.getDateComponents(date).map { it.toString().padStart(2, '0') }
}

private fun List<String>.toPath(): Path =
    joinToString(Path.DIRECTORY_SEPARATOR).toPath()
