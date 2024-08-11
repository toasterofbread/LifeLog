package dev.toastbits.lifelog.extension.mediawatch.impl.model.reference

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

data class GameMediaReference(
    override val mediaId: String,
    override val extensionId: ExtensionId,
    override val referenceTypeId: ExtensionId
): MediaReference() {
    override val mediaType: MediaEntityType = MediaEntityType.GAME
}
