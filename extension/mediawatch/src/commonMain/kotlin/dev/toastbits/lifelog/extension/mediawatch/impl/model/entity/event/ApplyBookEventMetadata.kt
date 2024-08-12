package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.alert.MediaWatchLogParseAlert
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.BookMediaConsumeEvent

internal fun applyBookEventMetadata(
    text: String,
    event: BookMediaConsumeEvent,
    strings: MediaWatchExtensionStrings,
    logStrings: LogFileConverterStrings,
    onAlert: (LogParseAlert) -> Unit
) {
    val parts: List<String> = text.split(',')

    for (part in parts) {
        if (applyEventReadRangeString(text, event, strings, logStrings, onAlert)) {
            continue
        }

        TODO("$part | $text")
    }
}

private fun applyEventReadRangeString(
    text: String,
    event: BookMediaConsumeEvent,
    strings: MediaWatchExtensionStrings,
    logStrings: LogFileConverterStrings,
    onAlert: (LogParseAlert) -> Unit
): Boolean {
    for (prefix in strings.bookReadVolumePrefixes) {
        if (text.startsWith(prefix)) {
            val (lhs, rhs) = parseMediaRangeString(text.drop(prefix.length).trimStart(), strings, onAlert)
            event.readRange = BookMediaConsumeEvent.ReadRange.Volumes(lhs, rhs)
            return true
        }
    }

    for (suffix in strings.bookReadVolumeSuffixes) {
        if (text.endsWith(suffix)) {
            val (lhs, rhs) = parseMediaRangeString(text.dropLast(suffix.length).trimEnd(), strings, onAlert)
            event.readRange = BookMediaConsumeEvent.ReadRange.Volumes(lhs, rhs)
            return true
        }
    }

    for (prefix in strings.bookPageRangePrefixes) {
        if (text.startsWith(prefix)) {
            val (lhs, rhs) = parseMediaRangeString(text.drop(prefix.length).trimStart(), strings, onAlert)
            event.readRange = BookMediaConsumeEvent.ReadRange.Pages(lhs, rhs)
            return true
        }
    }

    val range: BookMediaConsumeEvent.ReadRange? = strings.parseLowercaseBookReadRange(text, logStrings)
    if (range != null) {
        val eventReadRange = event.readRange
        if (eventReadRange != null) {
            val combined: BookMediaConsumeEvent.ReadRange? = eventReadRange.combineWith(range)
            if (combined != null) {
                event.readRange = combined
            }
            else {
                onAlert(MediaWatchLogParseAlert.IncompatibleBookReadRanges(strings.extensionId, eventReadRange, range))
            }
        }
        else {
            event.readRange = range
        }

        return true
    }

    return false
}
