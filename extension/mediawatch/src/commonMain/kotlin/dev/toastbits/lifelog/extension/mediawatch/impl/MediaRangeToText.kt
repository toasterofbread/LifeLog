package dev.toastbits.lifelog.extension.mediawatch.impl

import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.MediaRangeValue
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

internal fun MediaWatchExtensionStrings.mediaRangeToText(type: MediaEntityType, start: MediaRangeValue?, end: MediaRangeValue?): String? = buildString {
    val prefix: String =
        when (type) {
            MediaEntityType.MOVIE_OR_SHOW -> movieOrShowEpisodeRangePrefixes.first()
            MediaEntityType.BOOK -> this@mediaRangeToText.bookReadPagePrefixes.first()
            MediaEntityType.GAME -> ""
            MediaEntityType.SONG -> ""
        }

    if (start != null && end != null && start != end) {
        append(prefix)
        append(mediaRangeValueToText(start))

        if (
            start is MediaRangeValue.Discrete && end is MediaRangeValue.Discrete
            && (end.value.toInt() - start.value.toInt()) == 1
        ) {
            append(mediaRangeSplitterDefaultTwo)
        }
        else {
            append(mediaRangeSplitterDefaultMany)
        }

        append(mediaRangeValueToText(end))
    }
    else if (start != null) {
        if (end == null) {
            append(mediaDurationRangeFromPrefixes.first())
        }
        append(prefix)
        append(mediaRangeValueToText(start))
    }
    else if (end != null) {
        append(mediaDurationRangeToPrefixes.first())
        append(prefix)
        append(mediaRangeValueToText(end))
    }
    else {
        return null
    }
}
