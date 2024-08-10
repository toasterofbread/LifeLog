package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.extension.Extendable
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate
import okio.Path

interface LogDatabaseFileStructureProvider: Extendable, LogEntityReferenceParser {
    fun getLogFilePath(date: LocalDate): Path
    fun getEntityReferenceFilePath(reference: LogEntityReference): Path
}
