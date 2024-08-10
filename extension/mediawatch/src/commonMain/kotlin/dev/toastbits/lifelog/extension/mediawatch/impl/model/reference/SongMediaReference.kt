package dev.toastbits.lifelog.extension.mediawatch.impl.model.reference

import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import kotlin.reflect.KClass

data class SongMediaReference(
    override val mediaId: String
): MediaReference() {
    override val mediaType: MediaEntityType = MediaEntityType.SONG
    override val entityTypeClass: KClass<*> = MediaConsumeEventTypeImpl::class
}
