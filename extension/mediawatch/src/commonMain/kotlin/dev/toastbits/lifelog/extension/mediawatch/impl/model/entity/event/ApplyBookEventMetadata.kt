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
    val range: BookMediaConsumeEvent.ReadRange? = strings.parseLowercaseBookReadRange(text, logStrings, onAlert)
    if (range != null) {
        event.readRange = range
        return true
    }

    return false
}
