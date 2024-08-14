package dev.toastbits.lifelog.extension.mediawatch.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings

data class SongMediaConsumeEvent(
    override var mediaReference: MediaReference,
    override var inlineComment: UserContent? = null,
    override var aboveComment: UserContent? = null,
    override var content: UserContent? = null,
    override var iteration: Int? = null,
    override var iterationsUnsure: Boolean = false
): MediaConsumeEvent {
    override val mediaEntityType: MediaEntityType = MediaEntityType.SONG

    override fun generateMediaRangeMetadata(
        strings: MediaWatchExtensionStrings,
        logStrings: LogFileConverterStrings
    ): String? = null
}
