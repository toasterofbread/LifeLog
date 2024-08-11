package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogFileConverter
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.converter.parseOrNull
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

internal class LogFileParser(
    private val strings: LogFileConverterStrings,
    private val eventTypes: List<LogEventType>,
    private val userContentParser: UserContentParser,
    private val referenceParser: LogEntityReferenceParser
) {
    private lateinit var days: MutableMap<LogDate, MutableList<LogEvent>>
    private lateinit var alerts: MutableList<ParseAlertData>

    private lateinit var iterator: Iterator<String>
    private var currentLineIndex: Int = -1
    private var currentDay: LogDate? = null

    private var lastDayTopLevelComment: IndexedValue<LogComment>? = null

    private fun hasNext(): Boolean = iterator.hasNext()
    private fun goNext(): String = iterator.next().also { currentLineIndex++ }.also { println("LINE $it") }

    private fun onAlert(error: LogParseAlert, line: Int = currentLineIndex) {
        TODO("$error")
        alerts.add(ParseAlertData(error, line.toUInt(), null))
    }

    private fun String.extractComment(): Pair<String, UserContent?> {
        val commentStart: Int = indexOf(strings.commentPrefix)
        if (commentStart == -1) {
            return this to null
        }

        val comment: String = drop(commentStart + strings.commentPrefix.length).trim()
        return substring(0, commentStart).trim() to parseUserContent(comment)
    }

    private fun getDayEvents(): MutableList<LogEvent> {
        val day = currentDay
        if (day == null) {
            onAlert(LogParseAlert.LogEventOutsideDay)
            return mutableListOf()
        }
        return days.getOrPut(day) { mutableListOf() }
    }

    fun parse(lines: Iterable<String>): LogFileConverter.ParseResult {
        days = mutableMapOf()
        alerts = mutableListOf()
        iterator = lines.iterator()
        currentLineIndex = -1
        currentDay = null

        while (hasNext()) {
            val line: String = goNext().trim()
            parseTopLevelLine(line)
        }

        days.entries.removeAll { it.value.isEmpty() }

        return LogFileConverter.ParseResult(
            days = days,
            alerts = alerts
        )
    }

    private fun parseTopLevelLine(line: String) {
        if (line.isBlank()) {
            return
        }

        if (line.startsWith(strings.commentPrefix)) {
            val commentText: String = line.drop(strings.commentPrefix.length).trimStart()
            onCommentLine(parseUserContent(commentText))
            return
        }

        if (line.startsWith(strings.datePrefix)) {
            var (dateText, inlineComment) = line.drop(strings.datePrefix.length).extractComment()
            var ambiguous: Boolean = false

            if (dateText.lowercase().startsWith(strings.ambiguousDatePrefix.lowercase())) {
                ambiguous = true
                dateText = dateText.drop(strings.ambiguousDatePrefix.length).trimStart()
            }

            val date: LocalDate? = parseDate(dateText)
            onDateLine(date, ambiguous, inlineComment)

            return
        }

        for (eventType in eventTypes) {
            for ((index, prefix) in eventType.prefixes.withIndex()) {
                if (line.length < prefix.length) {
                    continue
                }

                if (line.take(prefix.length).lowercase() != prefix.lowercase()) {
                    continue
                }

                var eventLine: String = line.drop(prefix.length).trimStart()
                val comment: UserContent?

                val commentStart: Int = eventLine.indexOf(strings.commentPrefix)
                if (commentStart != -1) {
                    comment = parseUserContent(eventLine.substring(commentStart + strings.commentPrefix.length).trimStart())
                    eventLine = eventLine.substring(0, commentStart).trimEnd()
                }
                else {
                    comment = null
                }

                onEventLine(eventType, index, eventLine, comment)
                return
            }
        }

        onAlert(LogParseAlert.UnmatchedEventFormat(line, eventTypes.flatMap { it.prefixes }))
    }

    private fun parseDate(text: String): LocalDate? {
        for (dateFormat in strings.dateFormats) {
            val date: LocalDate = dateFormat.parseOrNull(text) ?: continue
            return date
        }

        onAlert(LogParseAlert.NoMatchingDateFormat(text))
        return null
    }

    private fun parseUserContent(text: String, lineOffset: Int = 0): UserContent {
        val newLines: Int = text.count { it == '\n' }
        return userContentParser.parseUserContent(
            text,
            referenceParser,
            onAlert = { alert, line -> onAlert(alert, currentLineIndex + line - newLines + lineOffset) }
        )
    }

    private fun getLastTopLevelCommentIfAdjacent(): UserContent? =
        lastDayTopLevelComment?.let { lastComment ->
            if (lastComment.index != currentLineIndex - 1) {
                return@let null
            }

            val events: MutableList<LogEvent> = getDayEvents()
            check(events.contains(lastComment.value))
            events.remove(lastComment.value)

            return@let lastDayTopLevelComment!!.value.content
        }

    private fun onDateLine(date: LocalDate?, ambiguous: Boolean, inlineComment: UserContent?) {
        if (date == null) {
            onAlert(LogParseAlert.MissingDateError)
            return
        }

        currentDay = LogDateImpl(
            date = date,
            ambiguous = ambiguous,
            inlineComment = inlineComment,
            aboveComment = getLastTopLevelCommentIfAdjacent()
        )
        getDayEvents()
    }

    private fun onEventLine(eventType: LogEventType, eventPrefixIndex: Int, line: String, inlineComment: UserContent?) {
        val body: String
        val metadata: String?
        val contentLines: MutableList<String> = mutableListOf()

        val aboveComment: UserContent? = getLastTopLevelCommentIfAdjacent()

        val metadataStart: Int = line.indexOf(strings.eventMetadataStart)
        val contentStart: Int = line.indexOf(strings.eventContentStart)

        if (metadataStart != -1 && (metadataStart < contentStart || contentStart == -1)) {
            val metadataEnd: Int = line.indexOf(strings.eventMetadataEnd, metadataStart)

            if (metadataEnd == -1) {
                onAlert(LogParseAlert.UnterminatedEventMetadata)
                return
            }

            body = line.substring(0, metadataStart).trim()
            metadata = line.substring(metadataStart + 1, metadataEnd).trim()
        }
        else {
            metadata = null

            if (contentStart == -1) {
                body = line
            }
            else {
                body = line.substring(0, contentStart)
            }
        }

        if (line.contains("もののけ姫")) {
            TODO("$metadataStart $contentStart | $line")
        }

        if (contentStart != -1) {
            var contentEnd: Int = line.indexOf(strings.eventContentEnd, contentStart)

            if (contentStart + 1 < line.length) {
                if (contentEnd != -1) {
                    // Content ends on this line
                    contentLines.add(line.substring(contentStart + 1, contentEnd))
                    return
                }
                else {
                    contentLines.add(line.substring(contentStart + 1))
                }
            }

            while (hasNext()) {
                val contentLine: String = goNext()
                contentEnd = contentLine.indexOf(strings.eventContentEnd)
                if (contentEnd != -1) {
                    contentLines.add(contentLine.substring(0, contentEnd))
                    break
                }
                else {
                    contentLines.add(contentLine)
                }
            }

            if (contentEnd == -1) {
                onAlert(LogParseAlert.EventContentNotTerminated)
            }
        }

        val content: UserContent? =
            parseUserContent(contentLines.joinToString("\n").trimIndent(), -1).takeIf { it.isNotEmpty() }

        val event: LogEvent = eventType.parseEvent(eventPrefixIndex, body, metadata, content, referenceParser, strings, ::onAlert)
        event.inlineComment = inlineComment
        event.aboveComment = aboveComment

        getDayEvents().add(event)
    }

    private fun onCommentLine(content: UserContent) {
        val comment: LogCommentImpl = LogCommentImpl(content)
        lastDayTopLevelComment = IndexedValue(currentLineIndex, comment)

        getDayEvents().add(comment)
    }
}
