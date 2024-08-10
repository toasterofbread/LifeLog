package dev.toastbits.lifelog.extension.media.impl

import dev.toastbits.lifelog.extension.media.MediaExtensionStrings

data class MediaExtensionStringsImpl(
    override val extensionIdentifier: String = "media",

    override val mediaReferenceTypeIdentifier: String = "media"
) : MediaExtensionStrings
