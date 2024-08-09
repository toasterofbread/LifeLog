package dev.toastbits.lifelog.extension.media.impl.model.reference

import dev.toastbits.lifelog.extension.media.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import kotlin.reflect.KClass

data class BookMediaReference(
    override val mediaId: String
): MediaReference() {
    override val mediaType: MediaEntityType = MediaEntityType.BOOK
    override val entityTypeClass: KClass<*> = MediaConsumeEventTypeImpl::class
}
