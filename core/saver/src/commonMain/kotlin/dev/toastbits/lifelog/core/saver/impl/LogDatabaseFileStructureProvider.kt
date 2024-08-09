package dev.toastbits.lifelog.core.saver.impl

import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.saver.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import kotlinx.datetime.LocalDate
import okio.Path
import okio.Path.Companion.toPath

class LogDatabaseFileStructureProviderImpl(
    private val splitStrategy: LogFileSplitStrategy,
    private val logFileName: String = "log.md",
    private val logsDirectoryName: String = "logs",
    private val logImagesDirectoryName: String = "images"
): LogDatabaseFileStructureProvider {
    override fun getLogFilePath(date: LocalDate): Path =
        (getLogFileDirectory(date) + listOf(logFileName)).toPath()

    override fun getLogImagesPath(date: LocalDate): Path =
        (getLogFileDirectory(date) + listOf(logImagesDirectoryName)).toPath()

    private fun getLogFileDirectory(date: LocalDate): List<String> =
        listOf(logsDirectoryName) + splitStrategy.getDateComponents(date).map { it.toString().padStart(2, '0') }
}

private fun List<String>.toPath(): Path =
    joinToString(Path.DIRECTORY_SEPARATOR).toPath()
