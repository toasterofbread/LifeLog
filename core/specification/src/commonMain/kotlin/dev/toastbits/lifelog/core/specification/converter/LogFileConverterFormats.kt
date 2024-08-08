package dev.toastbits.lifelog.core.specification.converter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

interface LogFileConverterFormats {
    val datePrefix: String
    val commentPrefix: String

    val eventMetadataStart: String
    val eventMetadataEnd: String
    val eventContentStart: String
    val eventContentEnd: String

    val preferredDateFormat: DateTimeFormat<LocalDate>
    val dateFormats: List<DateTimeFormat<LocalDate>>
}
