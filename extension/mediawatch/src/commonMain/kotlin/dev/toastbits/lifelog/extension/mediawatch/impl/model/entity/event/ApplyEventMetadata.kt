package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.SongMediaConsumeEvent

internal fun applyEventMetadata(
    text: String,
    event: MediaConsumeEvent,
    strings: MediaWatchExtensionStrings,
    onAlert: (LogParseAlert) -> Unit
) {
    val iterationSuffixes: List<String> = strings.getMediaEntityTypeIterationSuffixes(event.mediaEntityType).map { it.lowercase() }

    val parts: List<String> = text.split(',').filter { it.isNotBlank() }
    for (part in parts) {
        var lowerPart: String? = part.lowercase()

        for (suffix in iterationSuffixes) {
            if (!lowerPart!!.endsWith(suffix)) {
                continue
            }

            val iterationText: String = lowerPart.dropLast(suffix.length).trimEnd()
            applyEventIterationString(iterationText, event, onAlert)
            lowerPart = null
            break
        }

        if (lowerPart == null) {
            break
        }

        when (event) {
            is MovieOrShowMediaConsumeEvent -> applyMovieOrShowEventMetadata(lowerPart, event, strings, onAlert)
            is BookMediaConsumeEvent -> TODO(lowerPart)
            is GameMediaConsumeEvent -> TODO(lowerPart)
            is SongMediaConsumeEvent -> TODO(lowerPart)
        }
    }
}

private fun applyEventIterationString(
    text: String,
    event: MediaConsumeEvent,
    onAlert: (LogParseAlert) -> Unit
) {
    var number: Int? =
        when (text) {
            "1st",
            "first" -> 1
            "2nd",
            "second" -> 2
            "3rd",
            "third" -> 3
            "fourth" -> 4
            "fifth" -> 5
            "sixth" -> 6
            "seventh" -> 7
            "eighth" -> 8
            "ninth" -> 9
            "tenth" -> 10
            else -> null
        }

    if (number == null && text.endsWith("th")) {
        number = text.dropLast(2).trimEnd().toIntOrNull()
    }

    if (number == null) {
        onAlert(LogParseAlert.UnknownIterationSpecifier(text))
        return
    }

    event.iteration = number
}
