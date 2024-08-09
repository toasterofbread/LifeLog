package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentParser
import dev.toastbits.lifelog.core.specification.impl.model.reference.LogEntityReferenceGeneratorImpl
import dev.toastbits.lifelog.core.specification.impl.model.reference.LogEntityReferenceParserImpl
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

class LogFileConverterImpl(
    private val formats: LogFileConverterFormats = DEFAULT_FORMATS,
    eventTypes: List<LogEventType> = DEFAULT_EVENT_TYPES,
    referenceTypes: List<LogEntityReferenceType> = DEFAULT_REFERENCE_TYPES,
    private val userContentParser: UserContentParser = MarkdownUserContentParser(),
    private val userContentGenerator: UserContentGenerator = MarkdownUserContentGenerator()
): LogFileConverter {
    private val registeredEventTypes: MutableList<LogEventType> = eventTypes.toMutableList()
    private val registeredReferenceTypes: MutableList<LogEntityReferenceType> = referenceTypes.toMutableList()

    val referenceParser: LogEntityReferenceParser
        get() = LogEntityReferenceParserImpl(registeredEventTypes, registeredReferenceTypes)

    val referenceGenerator: LogEntityReferenceGenerator
        get() = LogEntityReferenceGeneratorImpl(registeredReferenceTypes)

    override fun registerExtension(specificationExtension: SpecificationExtension) {
        registeredEventTypes.addAll(specificationExtension.getExtraEventTypes())
        registeredReferenceTypes.addAll(specificationExtension.getExtraReferenceTypes())
    }

    override fun parseLogFile(lines: Iterable<String>): LogFileConverter.ParseResult =
        LogFileParser(
            formats,
            registeredEventTypes,
            userContentParser,
            referenceParser
        ).parse(lines)

    override fun generateLogFile(days: Map<LogDate, List<LogEvent>>): LogFileConverter.GenerateResult =
        LogFileGenerator(
            formats,
            registeredEventTypes,
            userContentGenerator,
            referenceGenerator
        ).generate(days)

    companion object {
        val DEFAULT_EVENT_TYPES: List<LogEventType> = listOf(

        )

        val DEFAULT_REFERENCE_TYPES: List<LogEntityReferenceType> = listOf(

        )

        val DEFAULT_FORMATS: LogFileConverterFormatsImpl =
            LogFileConverterFormatsImpl()
    }
}
