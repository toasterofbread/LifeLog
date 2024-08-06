package dev.toastbits.lifelog.specification.impl.converter

import dev.toastbits.lifelog.specification.converter.LogDatabaseConverter
import dev.toastbits.lifelog.specification.converter.LogDatabaseConverterFormats
import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.database.LogDatabase
import dev.toastbits.lifelog.specification.impl.converter.LogDatabaseConverterImpl.ParseResultData
import dev.toastbits.lifelog.specification.impl.converter.LogDatabaseConverterImpl.ParseAlertData
import dev.toastbits.lifelog.specification.impl.converter.usercontent.UserContentParser
import dev.toastbits.lifelog.specification.impl.model.entity.date.LogDateImpl
import dev.toastbits.lifelog.specification.impl.model.entity.event.LogCommentImpl
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.specification.model.entity.event.LogComment
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser
import kotlinx.datetime.LocalDate

internal class LogDatabaseParser(
    private val formats: LogDatabaseConverterFormats,
    private val eventTypes: List<LogEventType<*>>,
    private val userContentParser: UserContentParser,
    private val referenceParser: LogEntityReferenceParser
) {
    private lateinit var days: MutableMap<LogDate?, MutableList<LogEvent>>
    private lateinit var alerts: MutableList<ParseAlertData>

    private lateinit var iterator: Iterator<String>
    private var currentLineIndex: Int = -1
    private var currentDay: LogDate? = null

    private var lastDayTopLevelComment: IndexedValue<LogComment>? = null

    private fun hasNext(): Boolean = iterator.hasNext()
    private fun goNext(): String = iterator.next().also { currentLineIndex++ }

    private fun onAlert(error: LogParseAlert, line: Int = currentLineIndex) {
        alerts.add(ParseAlertData(error, line))
    }

    private fun String.extractComment(): Pair<String, UserContent?> {
        val commentStart: Int = indexOf(formats.commentPrefix)
        if (commentStart == -1) {
            return this to null
        }

        val comment: String = drop(commentStart + formats.commentPrefix.length).trim()
        return substring(0, commentStart).trim() to parseUserContent(comment)
    }

    private fun getDayEvents(): MutableList<LogEvent> =
        days.getOrPut(currentDay) { mutableListOf() }

    fun parse(lines: Iterable<String>): LogDatabaseConverter.ParseResult {
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

        return ParseResultData(
            database =
                object : LogDatabase {
                    override val days: MutableMap<LogDate?, MutableList<LogEvent>> = this@LogDatabaseParser.days
                },
            alerts = alerts
        )
    }

    private fun parseTopLevelLine(line: String) {
        if (line.isBlank()) {
            return
        }

        if (line.startsWith(formats.commentPrefix)) {
            val commentText: String = line.drop(formats.commentPrefix.length).trimStart()
            onCommentLine(parseUserContent(commentText))
            return
        }

        if (line.startsWith(formats.datePrefix)) {
            val (dateText, comment) = line.drop(formats.datePrefix.length).extractComment()
            val date: LocalDate? = parseDate(dateText)

            onDateLine(date, comment)
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

                val commentStart: Int = eventLine.indexOf(formats.commentPrefix)
                if (commentStart != -1) {
                    comment = parseUserContent(eventLine.substring(commentStart + formats.commentPrefix.length).trimStart())
                    eventLine = eventLine.substring(0, commentStart).trimEnd()
                }
                else {
                    comment = null
                }

                onEventLine(eventType, index, eventLine, comment)
                return
            }
        }

        onAlert(LogParseAlert.UnmatchedEventFormat(line))
    }

    private fun parseDate(text: String): LocalDate? {
        for (dateFormat in formats.dateFormats) {
            val date: LocalDate = dateFormat.parseOrNull(text) ?: continue
            return date
        }

        onAlert(LogParseAlert.NoMatchingDateFormat)
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

    private fun onDateLine(date: LocalDate?, commentContent: UserContent?) {
        if (date == null) {
            onAlert(LogParseAlert.MissingDateError)
            return
        }

        val comments: List<UserContent> =
            // If a top-level comment was placed directly above this date, make it this date's comment
            listOfNotNull(getLastTopLevelCommentIfAdjacent(), commentContent)

        currentDay = LogDateImpl(date, comments = comments)
        getDayEvents()
    }

    private fun onEventLine(eventType: LogEventType<*>, eventPrefixIndex: Int, line: String, inlineComment: UserContent?) {
        val body: String
        val metadata: String?
        val contentLines: MutableList<String> = mutableListOf()

        val comments: List<UserContent> =
            // If a top-level comment was placed directly above this event, make it this event's comment
            listOfNotNull(getLastTopLevelCommentIfAdjacent(), inlineComment)

        val metadataStart: Int = line.indexOf(formats.eventMetadataStart)
        val contentStart: Int = line.indexOf(formats.eventContentStart)

        if (metadataStart < contentStart || contentStart == -1) {
            val metadataEnd: Int = line.indexOf(formats.eventMetadataEnd, metadataStart)

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

        if (contentStart != -1) {
            var contentEnd: Int = line.indexOf(formats.eventContentEnd, contentStart)

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
                contentEnd = contentLine.indexOf(formats.eventContentEnd)
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

        val event: LogEvent = eventType.parseEvent(eventPrefixIndex, body, metadata, content, ::onAlert)
        event.comments += comments

        getDayEvents().add(event)
    }

    private fun onCommentLine(content: UserContent) {
        val comment: LogCommentImpl = LogCommentImpl(content)
        lastDayTopLevelComment = IndexedValue(currentLineIndex, comment)

        getDayEvents().add(comment)
    }

//    private fun <T> parseSuccess(result: T): InternalParseResult<T> =
//        InternalParseResult.Success(result)
//
//    private fun <T> parseError(error: ParseException): InternalParseResult<T> =
//        InternalParseResult.Failure(error)
}

//private sealed interface InternalParseResult<T> {
//    class Success<T>(val value: T): InternalParseResult<T> {
//        override fun handleError(block: (ParseException) -> Nothing): T = value
//    }
//    class Failure<T>(val error: ParseException): InternalParseResult<T> {
//        override fun handleError(block: (ParseException) -> Nothing): T = block(error)
//    }
//
//    fun handleError(block: (ParseException) -> Nothing): T
//}

//private class ParseException(
//    override val message: String,
//    cause: Throwable? = null
//): RuntimeException(message, cause)
