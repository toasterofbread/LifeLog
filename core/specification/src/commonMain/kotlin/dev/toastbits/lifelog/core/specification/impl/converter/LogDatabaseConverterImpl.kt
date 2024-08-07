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
    private val formats: dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverterFormats = dev.toastbits.lifelog.core.specification.impl.converter.LogDatabaseConverterImpl.Companion.DEFAULT_FORMATS,
    eventTypes: List<LogEventType<*>> = dev.toastbits.lifelog.core.specification.impl.converter.LogDatabaseConverterImpl.Companion.DEFAULT_EVENT_TYPES,
    referenceTypes: List<LogEntityReferenceType<*>> = dev.toastbits.lifelog.core.specification.impl.converter.LogDatabaseConverterImpl.Companion.DEFAULT_REFERENCE_TYPES,
    private val userContentParser: UserContentParser = MarkdownUserContentParser()
): dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter {
    private val registeredEventTypes: MutableList<LogEventType<*>> = eventTypes.toMutableList()
    private val registeredReferenceTypes: MutableList<LogEntityReferenceType<*>> = referenceTypes.toMutableList()

    override fun registerExtension(specificationExtension: dev.toastbits.lifelog.core.specification.extension.SpecificationExtension) {
        registeredEventTypes.addAll(specificationExtension.getExtraEventTypes())
        registeredReferenceTypes.addAll(specificationExtension.getExtraReferenceTypes())
    }

    override fun parseLogDatabase(lines: Iterable<String>): dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter.ParseResult {
        val referenceParser: LogEntityReferenceParser =
            LogEntityReferenceParserImpl(registeredEventTypes, registeredReferenceTypes)

        return dev.toastbits.lifelog.core.specification.impl.converter.LogDatabaseParser(
            formats,
            registeredEventTypes,
            userContentParser,
            referenceParser
        ).parse(lines)
    }

    companion object {
        val DEFAULT_EVENT_TYPES: List<LogEventType<*>> = listOf(

        )

        val DEFAULT_REFERENCE_TYPES: List<LogEntityReferenceType<*>> = listOf(

        )

        val DEFAULT_FORMATS: dev.toastbits.lifelog.core.specification.impl.converter.LogDatabaseConverterFormatsImpl =
            dev.toastbits.lifelog.core.specification.impl.converter.LogDatabaseConverterFormatsImpl()
    }

    data class ParseResultData(
        override val database: dev.toastbits.lifelog.core.specification.database.LogDatabase,
        override val alerts: List<dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter.ParseAlert>
    ) : dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter.ParseResult

    data class ParseAlertData(
        override val alert: dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert,
        override val lineIndex: Int
    ) : dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter.ParseAlert
}
