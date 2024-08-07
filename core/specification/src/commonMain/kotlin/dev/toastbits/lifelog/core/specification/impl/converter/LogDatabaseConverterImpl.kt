package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter
import dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverterFormats
import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentParser
import dev.toastbits.lifelog.core.specification.impl.model.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

class LogDatabaseConverterImpl(
    private val formats: LogDatabaseConverterFormats = DEFAULT_FORMATS,
    eventTypes: List<LogEventType> = DEFAULT_EVENT_TYPES,
    referenceTypes: List<LogEntityReferenceType> = DEFAULT_REFERENCE_TYPES,
    private val userContentParser: UserContentParser = MarkdownUserContentParser()
): LogDatabaseConverter {
    private val registeredEventTypes: MutableList<LogEventType> = eventTypes.toMutableList()
    private val registeredReferenceTypes: MutableList<LogEntityReferenceType> = referenceTypes.toMutableList()

    override fun registerExtension(specificationExtension: SpecificationExtension) {
        registeredEventTypes.addAll(specificationExtension.getExtraEventTypes())
        registeredReferenceTypes.addAll(specificationExtension.getExtraReferenceTypes())
    }

    override fun parseLogDatabase(lines: Iterable<String>): LogDatabaseConverter.ParseResult {
        val referenceParser: LogEntityReferenceParser =
            LogEntityReferenceParserImpl(registeredEventTypes, registeredReferenceTypes)

        return LogDatabaseParser(
            formats,
            registeredEventTypes,
            userContentParser,
            referenceParser
        ).parse(lines)
    }

    companion object {
        val DEFAULT_EVENT_TYPES: List<LogEventType> = listOf(

        )

        val DEFAULT_REFERENCE_TYPES: List<LogEntityReferenceType> = listOf(

        )

        val DEFAULT_FORMATS: LogDatabaseConverterFormatsImpl =
            LogDatabaseConverterFormatsImpl()
    }

    data class ParseResultData(
        override val database: LogDatabase,
        override val alerts: List<LogDatabaseConverter.ParseAlert>
    ) : LogDatabaseConverter.ParseResult

    data class ParseAlertData(
        override val alert: LogParseAlert,
        override val lineIndex: Int
    ) : LogDatabaseConverter.ParseAlert
}
