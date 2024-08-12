package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.GameMediaConsumeEvent

internal fun applyGameEventMetadata(
    text: String,
    event: GameMediaConsumeEvent,
    strings: MediaWatchExtensionStrings,
    logStrings: LogFileConverterStrings,
    onAlert: (LogParseAlert) -> Unit
) {
    val parts: List<String> = text.split(',')

    for (part in parts) {
        if (event.playedRange == null && applyEventPlayedRangeString(text, event, strings, logStrings, onAlert)) {
            continue
        }

        TODO("$part | $text")
    }
}

private fun applyEventPlayedRangeString(
    text: String,
    event: GameMediaConsumeEvent,
    strings: MediaWatchExtensionStrings,
    logStrings: LogFileConverterStrings,
    onAlert: (LogParseAlert) -> Unit
): Boolean {
    val range: GameMediaConsumeEvent.PlayedRange? = strings.parseLowercaseGamePlayedRange(text, logStrings)
    if (range != null) {
        event.playedRange = range
        return true
    }

    return false
}
