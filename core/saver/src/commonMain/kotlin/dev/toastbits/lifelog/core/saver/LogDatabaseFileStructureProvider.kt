package dev.toastbits.lifelog.core.saver

import kotlinx.datetime.LocalDate
import okio.Path

interface LogDatabaseFileStructureProvider {
    fun getLogFilePath(date: LocalDate): Path
    fun getLogImagesPath(date: LocalDate): Path
}
