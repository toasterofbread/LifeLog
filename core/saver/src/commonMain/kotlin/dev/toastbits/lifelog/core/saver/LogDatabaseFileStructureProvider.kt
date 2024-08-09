package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.specification.extension.Extendable
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import kotlinx.datetime.LocalDate
import okio.Path

interface LogDatabaseFileStructureProvider: Extendable {
    fun getLogFilePath(date: LocalDate): Path
    fun getLogImagesPath(date: LocalDate): Path

    fun getEntityReferencePath(reference: LogEntityReference): Path
}
