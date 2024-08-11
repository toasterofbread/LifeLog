package dev.toastbits.lifelog.extension.mediawatch.impl

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType

class MediaWatchExtensionStringsImpl(
    override val extensionId: ExtensionId = "mediawatch",
    override val mediaReferenceTypeId: ExtensionId = "media"
): MediaWatchExtensionStrings {
    override fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String> =
        when (mediaEntityType) {
            MediaEntityType.MOVIE_OR_SHOW -> listOf(" watch")
            MediaEntityType.BOOK -> listOf(" read")
            MediaEntityType.GAME -> listOf(" play", " attempt")
            MediaEntityType.SONG -> listOf(" listen")
        }

    override fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String> =
        when (mediaEntityType) {
            MediaEntityType.MOVIE_OR_SHOW -> listOf("Watched ")
            MediaEntityType.BOOK -> listOf("Read ")
            MediaEntityType.GAME -> listOf("Played ")
            MediaEntityType.SONG -> listOf("Listened to ")
        }
}