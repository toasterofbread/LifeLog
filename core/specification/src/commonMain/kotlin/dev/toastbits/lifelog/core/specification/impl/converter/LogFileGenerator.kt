package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.GenerateAlertData
import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterFormats
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentGenerator
import dev.toastbits.lifelog.core.specification.impl.converter.usercontent.UserContentParser
import dev.toastbits.lifelog.core.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.core.specification.impl.model.entity.event.LogCommentImpl
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogComment
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate

internal class LogFileGenerator(
    private val formats: LogFileConverterFormats,
    private val eventTypes: List<LogEventType>,
    private val userContentGenerator: UserContentGenerator,
    private val referenceGenerator: LogEntityReferenceGenerator
) {
    private lateinit var lines: MutableList<String>
    private lateinit var alerts: MutableList<GenerateAlertData>

    private var currentLineIndex: Int = 0
//    private var currentDay: LogDate? = null

    private fun onAlert(error: LogGenerateAlert, line: Int = currentLineIndex) {
        alerts.add(GenerateAlertData(error, line))
    }

    private fun UserContent.toText(): String =
        userContentGenerator.generateUserContent(this, referenceGenerator, ::onAlert)

    private fun lines(lines: List<String>, entity: LogEntity? = null) {
        if (lines.isEmpty()) {
            return
        }

        line(lines.first(), entity)

        for (i in 1 until lines.size) {
            line(lines[i])
        }
    }

    private fun line(content: String, entity: LogEntity? = null) {
        check(!content.contains('\n'))

        entity?.aboveComment?.also { aboveComment ->
            lines.add(formats.commentPrefix + aboveComment.toText())
            currentLineIndex++
        }

        val lineContent: String =
            entity?.inlineComment?.let { inlineComment ->
                formats.commentPrefix + inlineComment.toText()
            } ?: content

        lines.add(lineContent)

        currentLineIndex++
    }

    fun generate(days: Map<LogDate?, List<LogEvent>>): LogFileConverter.GenerateResult {
        lines = mutableListOf()
        alerts = mutableListOf()
        currentLineIndex = 0
//        currentDay = null

        val sortedDays: List<LogDate?> = days.keys.sortedBy { it?.date }
        for (date in sortedDays) {
            onDate(date)

            val events: List<LogEvent> = days[date]!!
            for (event in events) {
                onEvent(event)
            }
        }

        return LogFileConverter.GenerateResult(
            lines = lines,
            alerts = alerts
        )
    }

    private fun onDate(date: LogDate?) {
        if (date != null) {
            line(formats.datePrefix + formats.preferredDateFormat.format(date.date), date)
            line("")
        }
    }

    private fun onEvent(event: LogEvent) {
        for (eventType in eventTypes) {
            if (!eventType.canGenerateEvent(event)) {
                continue
            }

            val content: String = eventType.generateEvent(event, ::onAlert)
            lines(content.split('\n'), event)
            break
        }
    }
}
