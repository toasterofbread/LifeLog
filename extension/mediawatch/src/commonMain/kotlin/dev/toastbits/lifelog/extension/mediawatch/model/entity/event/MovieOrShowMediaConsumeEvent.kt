package dev.toastbits.lifelog.extension.mediawatch.model.entity.event

import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.MediaRangeValue
import kotlin.time.Duration

data class MovieOrShowMediaConsumeEvent(
    override var mediaReference: MediaReference,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null,
    override var content: UserContent? = null,
    override var iteration: Int? = null,
    override var iterationsUnsure: Boolean = false,
    var watchedRange: WatchedRange? = null,
    var watchedRangeUnsure: Boolean = false
): MediaConsumeEvent {
    override val mediaEntityType: MediaEntityType = MediaEntityType.MOVIE_OR_SHOW

    sealed interface WatchedRange {
        data class Episodes(
            val startEpisode: MediaRangeValue?,
            val endEpisode: MediaRangeValue?
        ): WatchedRange

        data class Times(val startTime: Duration?, val endTime: Duration?): WatchedRange

        data class Proportion(val proportion: Float, val offset: Float): WatchedRange
    }
}
