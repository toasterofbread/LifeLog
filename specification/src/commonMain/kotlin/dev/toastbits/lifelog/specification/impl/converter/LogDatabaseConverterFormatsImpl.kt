package dev.toastbits.lifelog.specification.impl.converter

import dev.toastbits.lifelog.specification.converter.LogDatabaseConverterFormats
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

data class LogDatabaseConverterFormatsImpl(
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
): LogDatabaseConverterFormats
