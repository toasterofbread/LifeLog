package dev.toastbits.lifelog.extension.mediawatch.model.reference

import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

abstract class MediaReference: LogEntityReference.InMetadata {
    abstract val mediaType: MediaEntityType
    abstract val mediaId: String

    override val path: LogEntityPath get() = LogEntityPath.of(mediaType.name, mediaId)
}
