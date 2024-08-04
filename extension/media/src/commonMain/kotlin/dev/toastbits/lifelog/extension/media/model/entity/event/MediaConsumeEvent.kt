package dev.toastbits.lifelog.extension.media.model.entity.event

import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent

interface MediaConsumeEvent: LogEvent {
    val mediaReference: LogEntityProperty<MediaReference, MediaStringId>

    class MediaReferenceProperty(override var value: MediaReference) : LogEntityProperty<MediaReference, MediaStringId> {
        override val name: MediaStringId get() = MediaStringId.Property.MediaConsumeEvent.MEDIA_REFERENCE
    }
}
