package dev.toastbits.lifelog.specification.impl.converter

import dev.toastbits.lifelog.specification.converter.LogDatabaseConverterFormats
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

data class LogDatabaseConverterFormatsData(
    override val datePrefix: String = "----- ",
    override val commentPrefix: String = "//",

    override val eventMetadataStart: String = "(",
    override val eventMetadataEnd: String = ")",
    override val eventContentStart: String = "{",
    override val eventContentEnd: String = "}",

    override val dateFormats: List<DateTimeFormat<LocalDate>>,
    override val preferredDateFormat: DateTimeFormat<LocalDate>
): LogDatabaseConverterFormats
