package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.extension.validate
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.MarkdownUserContentParser
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentParser
import dev.toastbits.lifelog.core.specification.impl.extension.ExtendableImpl
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate

private typealias TypePath = List<String>

class LogFileConverterImpl(
    private val referenceParser: LogEntityReferenceParser,
    private val referenceGeneratorProvider: (LocalDate) -> LogEntityReferenceGenerator,
    private val formats: LogFileConverterStrings = DEFAULT_FORMATS,
//    eventTypes: List<LogEventType> = DEFAULT_EVENT_TYPES,
//    referenceTypes: List<LogEntityReferenceType> = DEFAULT_REFERENCE_TYPES,
    private val userContentParser: UserContentParser = MarkdownUserContentParser(),
    private val userContentGenerator: UserContentGenerator = MarkdownUserContentGenerator()
): ExtendableImpl(), LogFileConverter {
    override fun parseLogFile(lines: Iterable<String>): LogFileConverter.ParseResult =
        LogFileParser(
            formats,
            extensions.flatMap { it.extraEventTypes },
            userContentParser,
            referenceParser
        ).parse(lines)

    override fun generateLogFile(days: Map<LogDate, List<LogEvent>>): LogFileConverter.GenerateResult =
        LogFileGenerator(
            formats,
            extensions.flatMap { it.extraEventTypes },
            userContentGenerator,
            referenceGeneratorProvider
        ).generate(days)

    companion object {
        val INITIAL_EVENT_TYPES: Map<TypePath, List<LogEventType>> = mapOf(

        )

        val DEFAULT_FORMATS: LogFileConverterStringsImpl =
            LogFileConverterStringsImpl()
    }
}
