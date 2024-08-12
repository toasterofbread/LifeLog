package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.converter.alert.SpecificationLogParseAlert
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings

internal fun parseMediaRangeString(
    text: String,
    strings: MediaWatchExtensionStrings,
    onAlert: (LogParseAlert) -> Unit
): Pair<MediaRangeValue?, MediaRangeValue?> {
    val singleValue: MediaRangeValue? = MediaRangeValue.fromString(text)
    if (singleValue != null) {
        return Pair(singleValue, singleValue)
    }

    val (splitIndex: Int, splitter: String) =
        // I'm not sure how this makes me feel?
        strings.mediaRangeSplitters.firstNotNullOfOrNull { splitter ->
            text.indexOf(splitter).takeIf { it != -1 }?.let { it to splitter }
        }.let { result ->
            if (result == null) {
                onAlert(SpecificationLogParseAlert.InvalidEpisodesSpecifier(text))
                return Pair(null, null)
            }
            return@let result
        }

    val lhs: MediaRangeValue? = MediaRangeValue.fromString(text.substring(0, splitIndex).trimEnd())
    if (lhs == null) {
        onAlert(SpecificationLogParseAlert.InvalidEpisodesSpecifier(text))
        return Pair(null, null)
    }

    val rhs: MediaRangeValue? = MediaRangeValue.fromString(text.substring(splitIndex + splitter.length).trimStart())
    if (rhs == null) {
        onAlert(SpecificationLogParseAlert.InvalidEpisodesSpecifier(text))
        return Pair(null, null)
    }

    return Pair(lhs, rhs)
}
