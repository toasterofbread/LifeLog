package dev.toastbits.lifelog.core.specification.converter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

interface LogFileConverterStrings {
    val metadataDirectoryName: String
    val metadataExtensionDirectoryName: String
    val logsDirectoryName: String
    val logFileName: String

    val contentIndentation: String
    val datePrefix: String
    val commentPrefix: String

    val eventMetadataStart: String
    val eventMetadataEnd: String
    val eventContentStart: String
    val eventContentEnd: String

    val preferredDateFormat: DateTimeFormat<LocalDate>
    val dateFormats: List<DateTimeFormat<LocalDate>>

    fun numberToIteration(number: Int): String

    fun validate() {
        metadataDirectoryName.checkPath("metadataDirectoryName")
        metadataExtensionDirectoryName.checkPath("extensionDirectoryName")
    }

    private fun String.checkPath(name: String) {
        check(isNotBlank()) { "Path $name is blank" }
        check(ILLEGAL_PATH_CHARS.none { this@checkPath.contains(it) }) { "Path $name '$this' contains illegal character(s)" }
    }

    companion object {
        const val ILLEGAL_PATH_CHARS: String = "/"
    }
}
