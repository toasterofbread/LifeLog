package dev.toastbits.lifelog.specification.converter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

interface LogDatabaseConverterFormats {
    val datePrefix: String
    val commentPrefix: String

    val eventMetadataStart: String
    val eventMetadataEnd: String
    val eventContentStart: String
    val eventContentEnd: String

    val dateFormats: List<DateTimeFormat<LocalDate>>
    val preferredDateFormat: DateTimeFormat<LocalDate>
}
