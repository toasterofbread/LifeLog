package dev.toastbits.lifelog.extension.media.impl.model.entity.event

import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent

class MediaConsumeEventImpl(
    initialMediaReference: MediaReference,
    initialContent: UserContent? = null,
    initialComments: List<UserContent> = emptyList()
): MediaConsumeEvent {
    override val mediaReference: LogEntityProperty<MediaReference, MediaStringId> = MediaConsumeEvent.MediaReferenceProperty(initialMediaReference)
    override val content: LogEvent.ContentProperty = LogEvent.ContentProperty(initialContent)
    override val comments: LogEntity.CommentsProperty = LogEntity.CommentsProperty(initialComments)
}
