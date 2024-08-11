package dev.toastbits.lifelog.core.specification.converter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder

interface LogDateFormat {
    fun format(date: LocalDate): String
    fun parse(input: String): LocalDate
}

fun LogDateFormat.parseOrNull(input: String): LocalDate? =
    try {
        parse(input)
    }
    catch (_: Throwable) {
        null
    }

fun logDateFormatOf(block: DateTimeFormatBuilder.WithDate.() -> Unit): LogDateFormat =
    OfDateTimeFormat(LocalDate.Format(block))

fun logDateFormatOf(format: DateTimeFormat<LocalDate>): LogDateFormat =
    OfDateTimeFormat(format)

fun customLogDateFormat(
    format: (LocalDate) -> String,
    parse: (String) -> LocalDate
): LogDateFormat =
    object : LogDateFormat {
        override fun format(date: LocalDate): String = format(date)

        override fun parse(input: String): LocalDate = parse(input)
    }

private class OfDateTimeFormat(val format: DateTimeFormat<LocalDate>): LogDateFormat {
    override fun format(date: LocalDate): String = format.format(date)
    override fun parse(input: String): LocalDate = format.parse(input)
}

