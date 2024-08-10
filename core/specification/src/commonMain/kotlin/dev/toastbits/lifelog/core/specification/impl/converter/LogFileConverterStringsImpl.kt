package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

data class LogFileConverterStringsImpl(
    override val metadataDirectoryName: String = "metadata",
    override val metadataExtensionDirectoryName: String = "extension",
    override val logsDirectoryName: String = "logs",
    override val logFileName: String = "log.md",

    override val contentIndentation: String = "  ",
    override val datePrefix: String = "----- ",
    override val commentPrefix: String = "//",

    override val eventMetadataStart: String = "(",
    override val eventMetadataEnd: String = ")",
    override val eventContentStart: String = "{",
    override val eventContentEnd: String = "}",

    override val preferredDateFormat: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO,
    override val dateFormats: List<DateTimeFormat<LocalDate>> =
        listOf(
            LocalDate.Formats.ISO,

            // 04 August 2024
            LocalDate.Format {
                dayOfMonth(Padding.ZERO)
                char(' ')
                monthName(MonthNames.ENGLISH_FULL)
                char(' ')
                year()
            },
            // 4 August 2024
            LocalDate.Format {
                dayOfMonth(Padding.NONE)
                char(' ')
                monthName(MonthNames.ENGLISH_FULL)
                char(' ')
                year()
            }
        )
): LogFileConverterStrings {
    override fun numberToIteration(number: Int): String =
        when (number) {
            1 -> "first"
            2 -> "second"
            3 -> "third"
            4 -> "fourth"
            5 -> "fifth"
            6 -> "sixth"
            7 -> "seventh"
            8 -> "eighth"
            9 -> "ninth"
            10 -> "tenth"
            else -> "${number}th"
        }
}
