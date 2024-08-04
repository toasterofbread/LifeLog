package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.specification.model.reference.LogEntityReference

interface MediaReference: LogEntityReference<MediaConsumeEvent> {
    val mediaType: MediaEntityType
    val mediaId: String
}
