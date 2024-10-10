package dev.toastbits.lifelog.extension.mediawatch.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings

data class BookMediaConsumeEvent(
    override var mediaReference: MediaReference,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null,
    override var content: UserContent? = null,
    override var iteration: Int? = null,
    override var iterationsUnsure: Boolean = false,
    var readRange: ReadRange? = null
): MediaConsumeEvent {
    override val mediaEntityType: MediaEntityType = MediaEntityType.BOOK

    override fun getIcon(): LogEvent.Icon = LogEvent.Icon.MenuBook

    override fun generateMediaRangeMetadata(
        strings: MediaWatchExtensionStrings,
        logStrings: LogFileConverterStrings
    ): String? = buildString {
        val range: ReadRange = readRange ?: return null
        return buildString {
            if (range.unsure) {
                append(strings.unsurePrefixes.first())
            }
            append(strings.readRangeToText(range.start, range.end))
        }
    }

    data class ReadRange(val start: ReadPoint?, val end: ReadPoint?, val unsure: Boolean)

    sealed interface ReadPoint {
        data class Position(val volume: UInt?, val subpoint: Subpoint?): ReadPoint {
            init {
                require(volume != null || subpoint != null)
            }

            sealed interface Subpoint {
                data class Chapter(val chapter: UInt): Subpoint
                data class Page(val page: UInt): Subpoint
            }
        }

        data object Start: ReadPoint
        data object End: ReadPoint
    }

    private fun MediaWatchExtensionStrings.readPointToText(point: ReadPoint, includePrefixes: Boolean = true): String =
        when (point) {
            is ReadPoint.Position -> buildString {
                if (point.volume != null) {
                    if (includePrefixes) {
                        append(bookReadVolumePrefixes.first())
                    }
                    append(point.volume)
                    if (point.subpoint != null) {
                        append(' ')
                    }
                }

                when (point.subpoint) {
                    is ReadPoint.Position.Subpoint.Chapter -> {
                        if (includePrefixes) {
                            append(bookReadChapterPrefixes.first())
                        }
                        append(point.subpoint.chapter)
                    }
                    is ReadPoint.Position.Subpoint.Page -> {
                        if (includePrefixes) {
                            append(this@readPointToText.bookReadPagePrefixes.first())
                        }
                        append(point.subpoint.page)
                    }
                    null -> {}
                }
            }
            ReadPoint.Start -> mediaRangeStart.first()
            ReadPoint.End -> mediaRangeEnd.first()
        }

    private fun MediaWatchExtensionStrings.readRangeToText(start: ReadPoint?, end: ReadPoint?): String? = buildString {
        if (end == ReadPoint.Position(volume = null, subpoint = ReadPoint.Position.Subpoint.Page(1U)) && start == null) {
            return mediaRangeFirstPrefixes.first() + bookReadPagePrefixes.first().trimEnd()
        }

        if (start != null && end != null) {
            val sharedPrefix: Boolean = start.canUseSharedPrefix(end)
            if (sharedPrefix) {
                append(this@readRangeToText.bookReadPagePrefixes.first())
            }

            append(readPointToText(start, includePrefixes = !sharedPrefix))
            append(" $mediaRangeSplitterDefaultMany ")
            append(readPointToText(end, includePrefixes = !sharedPrefix))
        }
        else if (start != null) {
            append(mediaDurationRangeFromPrefixes.first())
            append(readPointToText(start))
        }
        else if (end != null) {
            append(mediaDurationRangeToPrefixes.first())
            append(readPointToText(end))
        }
        else {
            return null
        }
    }

    private fun ReadPoint.canUseSharedPrefix(with: ReadPoint): Boolean {
        if (this::class != with::class) {
            return false
        }
        return when (this) {
            is ReadPoint.Position -> {
                check(with is ReadPoint.Position)
                listOf(volume != null, subpoint != null) == listOf(with.volume != null, with.subpoint != null)
            }
            ReadPoint.Start -> false
            ReadPoint.End -> false
        }
    }
}
