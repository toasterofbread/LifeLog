package dev.toastbits.lifelog.extension.mediawatch.converter

import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

interface MediaExtensionConverterFormats {
    val extensionIdentifier: String

    val mediaReferenceTypeIdentifier: String

    fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String>
    fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String>
}
