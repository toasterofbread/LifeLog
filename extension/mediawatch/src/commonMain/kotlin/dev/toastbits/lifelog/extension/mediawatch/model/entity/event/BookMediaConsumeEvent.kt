package dev.toastbits.lifelog.extension.mediawatch.model.entity.event

import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.MediaRangeValue

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

    sealed interface ReadRange {
        fun combineWith(other: ReadRange): ReadRange? = null

        data class Volumes(
            val startVolume: MediaRangeValue?,
            val endVolume: MediaRangeValue?,
            val startChapter: MediaRangeValue? = null,
            val endChapter: MediaRangeValue? = null
        ): ReadRange {
            override fun combineWith(other: ReadRange): Volumes? {
                if (other !is Volumes) {
                    return null
                }

                return Volumes(
                    startVolume = other.startVolume ?: startVolume,
                    endVolume = other.endVolume ?: endVolume,
                    startChapter = other.startChapter ?: startChapter,
                    endChapter = other.endChapter ?: endChapter
                )
            }
        }

        data class Pages(val startPage: MediaRangeValue?, val endPage: MediaRangeValue?): ReadRange

        data object Start: ReadRange
        data object End: ReadRange
//        data class Pages(val startPage: UInt?, val endPage: Int?): ReadRange
    }
}
