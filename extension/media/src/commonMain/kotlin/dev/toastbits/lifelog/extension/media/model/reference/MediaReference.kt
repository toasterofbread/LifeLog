package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType

abstract class MediaReference: LogEntityReference {
    abstract val mediaType: MediaEntityType
    abstract val mediaId: String

    override val entityPath: LogEntityPath get() = LogEntityPath.of(mediaType.name, mediaId)
}
