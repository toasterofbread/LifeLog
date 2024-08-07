package dev.toastbits.lifelog.extension.media.model.entity.event

import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent

data class SongMediaConsumeEvent(
    override var mediaReference: MediaReference,
    override var comments: List<UserContent> = emptyList(),
    override var content: UserContent? = null,
    override var iteration: Int = 1
): MediaConsumeEvent {
    override val mediaEntityType: MediaEntityType = MediaEntityType.SONG
}
