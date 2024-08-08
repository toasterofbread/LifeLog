package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentParser
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.impl.model.entity.event.LogCommentImpl
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogComment
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate

internal class LogFileGenerator(
    private val formats: LogFileConverterFormats,
    private val eventTypes: List<LogEventType>,
    private val userContentParser: UserContentParser,
    private val referenceParser: LogEntityReferenceParser
) {
    private lateinit var lines: MutableList<String>
    private lateinit var alerts: MutableList<GenerateAlertData>

    fun generate(days: Map<LogDate?, List<LogEvent>>): LogFileConverter.GenerateResult {
        TODO()

        return LogFileConverter.GenerateResult(
            lines = lines,
            alerts = alerts
        )
    }
}
