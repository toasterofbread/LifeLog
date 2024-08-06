package dev.toastbits.lifelog.extension.media.converter

import dev.toastbits.lifelog.extension.media.util.MediaEntityType

interface MediaExtensionConverterFormats {
    fun getMediaEntityTypeIterationSuffixes(mediaEntityType: MediaEntityType): List<String>
    fun getMediaEntityTypeConsumeEventPrefixes(mediaEntityType: MediaEntityType): List<String>
}
