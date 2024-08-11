package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.converter.alert.SpecificationLogParseAlert
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MovieOrShowMediaConsumeEvent

internal fun applyMovieOrShowEventMetadata(
    text: String,
    event: MovieOrShowMediaConsumeEvent,
    strings: MediaWatchExtensionStrings,
    onAlert: (LogParseAlert) -> Unit
) {
    for (prefix in strings.episodeRangePrefixes) {
        if (!text.startsWith(prefix)) {
            continue
        }

        val episodeRangeText: String = text.drop(prefix.length).trimStart()
        applyEventEpisodesString(episodeRangeText, event, onAlert)
        return
    }

    TODO(text)
}

private fun applyEventEpisodesString(
    text: String,
    event: MovieOrShowMediaConsumeEvent,
    onAlert: (LogParseAlert) -> Unit
) {
    val splitChars: List<Char> = listOf('-', '~')

    val splitIndex: Int = text.indexOfFirst { splitChars.contains(it) }
    if (splitIndex == -1) {
        onAlert(SpecificationLogParseAlert.InvalidEpisodesSpecifier(text))
        return
    }

    val lhsText: String = text.substring(0, splitIndex)
    val lastWhitespace: Int = lhsText.lastIndexOf(' ')
    val lhs: Int? = lhsText.substring(lastWhitespace + 1).toIntOrNull()
    if (lhs == null) {
        onAlert(SpecificationLogParseAlert.InvalidEpisodesSpecifier(text))
        return
    }

    val rhsText: String = text.substring(splitIndex + 1).split(' ', limit = 2).first()
    val rhs: Int? = rhsText.toIntOrNull()
    if (rhs == null) {
        onAlert(SpecificationLogParseAlert.InvalidEpisodesSpecifier(text))
        return
    }

    event

    TODO(text)
}

