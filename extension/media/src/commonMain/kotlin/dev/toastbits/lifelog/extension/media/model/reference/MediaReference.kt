package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference

interface MediaReference: LogEntityReference {
    val mediaType: MediaEntityType
    val mediaId: String
}
