package dev.toastbits.lifelog.extension.mediawatch.model.entity.event

import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent
import kotlinx.datetime.LocalDate
import kotlin.time.Duration

data class GameMediaConsumeEvent(
    override var mediaReference: MediaReference,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null,
    override var content: UserContent? = null,
    override var iteration: Int? = null,
    override var iterationsUnsure: Boolean = false,
    var playedRange: PlayedRange? = null
): MediaConsumeEvent {
    override val mediaEntityType: MediaEntityType = MediaEntityType.GAME

    sealed interface PlayedRange {
        val unsure: Boolean

        data class Duration(val duration: kotlin.time.Duration, override val unsure: Boolean = false): PlayedRange
        data object Start: PlayedRange {
            override val unsure: Boolean = false
        }
        data object End: PlayedRange {
            override val unsure: Boolean = false
        }
        data class Days(val startDay: LocalDate?, val endDay: LocalDate?, override val unsure: Boolean = false): PlayedRange
    }
}
