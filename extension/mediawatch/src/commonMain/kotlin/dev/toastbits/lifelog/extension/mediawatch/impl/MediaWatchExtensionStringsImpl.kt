package dev.toastbits.lifelog.extension.mediawatch.impl

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.parseOrNull
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.impl.model.MediaDurationFormat
import dev.toastbits.lifelog.extension.mediawatch.impl.model.customMediaDurationFormat
import dev.toastbits.lifelog.extension.mediawatch.impl.model.durationFormatOf
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.MediaRangeValue
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.mediaRangeValue
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.parseMediaRangeString
import dev.toastbits.lifelog.extension.mediawatch.impl.model.parseOrNull
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.format.optional
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class MediaWatchExtensionStringsImpl(
    override val extensionId: ExtensionId = "mediawatch",
    override val mediaReferenceTypeId: ExtensionId = "media",

    override val unsurePrefixes: List<String> = listOf("about ", "at least "),

    override val mediaRangeSplitters: List<String> = listOf("~", "-", "〰", "〜", "&", "and"),
    override val mediaRangeFirstPrefixes: List<String> = listOf("up to ", "first "),
    override val mediaPreferredDurationFormat: MediaDurationFormat = durationFormatOf(LocalTime.Formats.ISO),
    override val mediaDurationFormats: List<MediaDurationFormat> = listOf(
        durationFormatOf(LocalTime.Formats.ISO),
        durationFormatOf {
            hour(Padding.NONE)
            char('h')
            optional {
                char(' ')
            }
            minute(Padding.NONE)
            char('m')
        },
        durationFormatOf {
            hour(Padding.NONE)
            char(':')
            minute()
            char(':')
            second()
        },
        customMediaDurationFormat(
            format = { duration ->
                "${duration.inWholeMinutes} minutes"
            },
            parse = { input ->
                check(input.lowercase().endsWith(" minutes"))
                input.dropLast(8).trimEnd().toInt().minutes
            }
        )
    ),

    override val movieOrShowEpisodeRangePrefixes: List<String> = listOf("ep ", "eps ", "episode ", "episodes "),
    override val movieOrShowDurationRangeFromPrefixes: List<String> = listOf("from ", "starting ", "starting at "),

    override val bookPageRangePrefixes: List<String> = listOf("pg ", "pgs ", "page ", "pages "),

    override val bookReadVolumeSuffixes: List<String> = listOf("巻"),
    override val bookReadVolumePrefixes: List<String> = listOf("volume ")
): MediaWatchExtensionStrings {
    override fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String> =
        when (mediaEntityType) {
            MediaEntityType.MOVIE_OR_SHOW -> listOf(" watch")
            MediaEntityType.BOOK -> listOf(" read")
            MediaEntityType.GAME -> listOf(" play", " attempt", " run")
            MediaEntityType.SONG -> listOf(" listen")
        }

    override fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String> =
        when (mediaEntityType) {
            MediaEntityType.MOVIE_OR_SHOW -> listOf("Watched ")
            MediaEntityType.BOOK -> listOf("Read ")
            MediaEntityType.GAME -> listOf("Played ")
            MediaEntityType.SONG -> listOf("Listened to ")
        }

    override fun parseLowercaseMovieOrShowWatchedRange(text: String, strings: LogFileConverterStrings): MovieOrShowMediaConsumeEvent.WatchedRange? {
        when (text) {
            "start" -> return MovieOrShowMediaConsumeEvent.WatchedRange.Episodes(startEpisode = 1.mediaRangeValue, endEpisode = null)
            "end" -> return MovieOrShowMediaConsumeEvent.WatchedRange.Episodes(startEpisode = null, endEpisode = (-1).mediaRangeValue)
            "完" -> return MovieOrShowMediaConsumeEvent.WatchedRange.Episodes(startEpisode = null, endEpisode = (-1).mediaRangeValue)
            "first half",
            "half" -> return MovieOrShowMediaConsumeEvent.WatchedRange.Proportion(proportion = 0.5f, offset = 0f)
            "second half" -> return MovieOrShowMediaConsumeEvent.WatchedRange.Proportion(proportion = 0.5f, offset = 0.5f)
            "first episode" -> return MovieOrShowMediaConsumeEvent.WatchedRange.Episodes(startEpisode = 1.mediaRangeValue, endEpisode = 1.mediaRangeValue)
        }

        if (text.endsWith(" episodes")) {
            var text: String = text.dropLast(9).trimEnd()
            var first: Boolean = true

            if (text.startsWith("first ")) {
                text = text.drop(6).trimStart()
            }
            else if (text.startsWith("last ")) {
                text = text.drop(5).trimStart()
                first = false
            }

            val singleValue: MediaRangeValue? = MediaRangeValue.fromString(text)
            if (singleValue != null) {
                if (first) {
                    return MovieOrShowMediaConsumeEvent.WatchedRange.Episodes(startEpisode = 1.mediaRangeValue, endEpisode = singleValue)
                }
                else {
                    return MovieOrShowMediaConsumeEvent.WatchedRange.Episodes(startEpisode = -singleValue, endEpisode = (-1).mediaRangeValue)
                }
            }
        }

        return null
    }

    override fun parseLowercaseBookReadRange(text: String, strings: LogFileConverterStrings): BookMediaConsumeEvent.ReadRange? {
        when (text) {
            "start" -> return BookMediaConsumeEvent.ReadRange.Start
            "end" -> return BookMediaConsumeEvent.ReadRange.End
        }

        if (text.startsWith("book ") || text.startsWith("books ")) {
            val (lhs: MediaRangeValue?, rhs: MediaRangeValue?) = parseMediaRangeString(text.split(' ', limit = 2).first().trimStart(), this, {})
            return BookMediaConsumeEvent.ReadRange.Volumes(startVolume = lhs, endVolume = rhs)
        }

        if (text.startsWith("chapter ") || text.startsWith("chapters ")) {
            val (lhs: MediaRangeValue?, rhs: MediaRangeValue?) = parseMediaRangeString(text.split(' ', limit = 2).first().trimStart(), this, {})
            return BookMediaConsumeEvent.ReadRange.Volumes(
                startVolume = null,
                endVolume = null,
                startChapter = lhs,
                endChapter = rhs
            )
        }

        if (text.startsWith("first ") && text.endsWith(" pages")) {
            val rangeValue: MediaRangeValue? = MediaRangeValue.fromString(text.substring(6, text.length - 7).trim())
            if (rangeValue != null) {
                return BookMediaConsumeEvent.ReadRange.Pages(startPage = 1.mediaRangeValue, endPage = rangeValue)
            }
        }

        return null
    }

    override fun parseLowercaseGamePlayedRange(text: String, strings: LogFileConverterStrings): GameMediaConsumeEvent.PlayedRange? {
        when (text) {
            "start" -> return GameMediaConsumeEvent.PlayedRange.Start
            "end" -> return GameMediaConsumeEvent.PlayedRange.End
        }

        var text: String = text

        var upTo: Boolean = false
        var unsure: Boolean = false

        if (text.startsWith("up to ")) {
            upTo = true
            text = text.drop(6).trimStart()
        }

        for (prefix in unsurePrefixes) {
            if (text.startsWith(prefix)) {
                unsure = true
                text = text.drop(prefix.length).trimStart()
                break
            }
        }

        val duration: Duration? = mediaDurationFormats.firstNotNullOfOrNull { it.parseOrNull(text) }
        if (duration != null) {
            return GameMediaConsumeEvent.PlayedRange.Duration(duration, unsure = unsure)
        }

        var date: LocalDate? = strings.dateFormats.firstNotNullOfOrNull { it.parseOrNull(text) }
        if (date == null) {
            val split: List<String> = text.split('/')
            if (split.size == 2) {
                val month: Int? = split[0].toIntOrNull()
                val day: Int? = split[1].toIntOrNull()
                if (month != null && day != null) {
                    date = LocalDate(0, month, day)
                }
            }
        }

        if (date != null) {
            if (upTo) {
                return GameMediaConsumeEvent.PlayedRange.Days(startDay = null, endDay = date, unsure = unsure)
            }
            else {
                return GameMediaConsumeEvent.PlayedRange.Days(startDay = date, endDay = null, unsure = unsure)
            }
        }

        return null
    }
}