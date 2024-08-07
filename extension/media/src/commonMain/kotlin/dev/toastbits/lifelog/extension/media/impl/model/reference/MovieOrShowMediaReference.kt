package dev.toastbits.lifelog.extension.media.impl.model.reference

import dev.toastbits.lifelog.extension.media.model.entity.event.MediaConsumeEvent
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.database.LogDatabase

data class MovieOrShowMediaReference(
    override val mediaId: String
): MediaReference {
    override val mediaType: MediaEntityType = MediaEntityType.MOVIE_OR_SHOW
    override fun getEntity(database: dev.toastbits.lifelog.core.specification.database.LogDatabase): MediaConsumeEvent {
        TODO(mediaId)
    }
}
