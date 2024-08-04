package dev.toastbits.lifelog.extension.media.impl.model.reference

import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.specification.database.LogDatabase

data class MediaReferenceImpl(
    override val mediaType: MediaEntityType,
    override val mediaId: String
) : MediaReference {
    override fun getEntity(database: LogDatabase): MediaConsumeEvent {
        TODO("$mediaType $mediaId")
    }
}
