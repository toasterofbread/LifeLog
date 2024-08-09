package dev.toastbits.lifelog.core.saver.reference

import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import kotlinx.datetime.LocalDate
import okio.Path

class LogEntityReferenceGeneratorImpl(
    private val fileStructureProvider: LogDatabaseFileStructureProvider,
    private val relativeToDate: LocalDate
): LogEntityReferenceGenerator {
    override fun generateReferencePath(
        reference: LogEntityReference,
        onAlert: (LogGenerateAlert) -> Unit
    ): LogEntityPath {
        val logPath: Path = fileStructureProvider.getLogFilePath(relativeToDate)
        val referencePath: Path = fileStructureProvider.getEntityReferencePath(reference)
        return LogEntityPath(referencePath.relativeTo(logPath).segments)
    }
}
