package dev.toastbits.lifelog.extension.media.impl

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.media.MediaExtensionStrings

data class MediaExtensionStringsImpl(
    override val extensionId: ExtensionId = "media",

    override val mediaReferenceTypeIdentifier: String = "media"
) : MediaExtensionStrings
