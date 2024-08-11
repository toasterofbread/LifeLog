package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.Extendable
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate
import okio.Path

interface DatabaseFileStructureProvider: LogEntityReferenceParser, Extendable {
    fun getLogFilePathSize(): Int

    fun getLogFilePath(date: LocalDate): Path
    fun getPathLogFile(path: List<String>, onAlert: (LogParseAlert) -> Unit): LogEntityReference.InLog?

    fun getEntityReferenceFilePath(reference: LogEntityReference): Path
}
