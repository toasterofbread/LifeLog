package dev.toastbits.lifelog.extension.media.model.entity.event

import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent

data class MovieOrShowMediaConsumeEvent(
    override var mediaReference: MediaReference,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null,
    override var content: UserContent? = null,
    override var iteration: Int? = null
): MediaConsumeEvent {
    override val mediaEntityType: MediaEntityType = MediaEntityType.MOVIE_OR_SHOW
}
