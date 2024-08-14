package dev.toastbits.lifelog.extension.mediawatch.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReference
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.extension.mediawatch.util.MediaStringId
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity
import dev.toastbits.lifelog.core.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtensionStrings

sealed interface MediaConsumeEvent: LogEvent {
    var mediaReference: MediaReference
    var iteration: Int?
    var iterationsUnsure: Boolean
    val mediaEntityType: MediaEntityType

    fun generateMediaRangeMetadata(
        strings: MediaWatchExtensionStrings,
        logStrings: LogFileConverterStrings
    ): String?

    companion object: LogEntityCompanion<MediaConsumeEvent>(LogEvent) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> =
            listOf(
                MediaStringId.Property.MediaConsumeEvent.MEDIA_REFERENCE.property { mediaReference },
                MediaStringId.Property.MediaEntity.ITERATION.property { iteration }
            )
    }
}
