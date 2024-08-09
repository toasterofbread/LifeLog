package dev.toastbits.lifelog.extension.media.impl.converter

import dev.toastbits.lifelog.extension.media.converter.MediaExtensionConverterFormats
import dev.toastbits.lifelog.extension.media.util.MediaEntityType

class MediaExtensionConverterFormatsImpl: MediaExtensionConverterFormats {
    override fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String> =
        when (mediaEntityType) {
            MediaEntityType.MOVIE_OR_SHOW -> listOf(" watch")
            MediaEntityType.BOOK -> listOf(" read")
            MediaEntityType.GAME -> listOf(" play")
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