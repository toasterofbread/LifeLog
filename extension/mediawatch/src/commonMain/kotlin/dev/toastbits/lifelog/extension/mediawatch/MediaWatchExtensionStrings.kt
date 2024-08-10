package dev.toastbits.lifelog.extension.mediawatch

import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

interface MediaWatchExtensionStrings {
    val extensionName: String

    val mediaReferenceTypeIdentifier: String

    fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String>
    fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String>
}
