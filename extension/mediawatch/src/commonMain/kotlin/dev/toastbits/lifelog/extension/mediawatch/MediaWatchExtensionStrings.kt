package dev.toastbits.lifelog.extension.mediawatch

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

interface MediaWatchExtensionStrings {
    val extensionId: String

    val mediaReferenceTypeId: ExtensionId

    val episodeRangePrefixes: List<String>
    val unsureIterationsPrefix: String

    fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String>
    fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String>
}
