package dev.toastbits.lifelog.extension.mediawatch

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.mediawatch.impl.model.MediaDurationFormat
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.BookMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.GameMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MovieOrShowMediaConsumeEvent
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

interface MediaWatchExtensionStrings {
    val extensionId: String

    val mediaReferenceTypeId: ExtensionId
    val mediaPreferredDurationFormat: MediaDurationFormat
    val mediaDurationFormats: List<MediaDurationFormat>

    val mediaRangeSplitters: List<String>
    val mediaRangeFirstPrefixes: List<String>
    val unsurePrefixes: List<String>

    val movieOrShowEpisodeRangePrefixes: List<String>
    val movieOrShowDurationRangeFromPrefixes: List<String>

    val bookPageRangePrefixes: List<String>

    val bookReadVolumeSuffixes: List<String>
    val bookReadVolumePrefixes: List<String>

    fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String>
    fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String>

    fun parseLowercaseMovieOrShowWatchedRange(text: String, strings: LogFileConverterStrings): MovieOrShowMediaConsumeEvent.WatchedRange?
    fun parseLowercaseBookReadRange(text: String, strings: LogFileConverterStrings): BookMediaConsumeEvent.ReadRange?
    fun parseLowercaseGamePlayedRange(text: String, strings: LogFileConverterStrings): GameMediaConsumeEvent.PlayedRange?
}
