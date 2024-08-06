package dev.toastbits.lifelog.extension.media.model.entity.event

import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.util.MediaEntityType
import dev.toastbits.lifelog.extension.media.util.MediaStringId
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.entity.LogEntity
import dev.toastbits.lifelog.specification.model.entity.LogEntityCompanion
import dev.toastbits.lifelog.specification.model.entity.LogEntityProperty
import dev.toastbits.lifelog.specification.model.entity.LogEntityPropertyData
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent

interface MediaConsumeEvent: LogEvent {
    var mediaReference: MediaReference
    var iteration: Int
    val mediaEntityType: MediaEntityType

    companion object: LogEntityCompanion<MediaConsumeEvent>(LogEvent) {
        override fun getAllProperties(): List<LogEntity.Property<*, *>> =
            listOf(
                MediaStringId.Property.MediaConsumeEvent.MEDIA_REFERENCE.property { mediaReference },
                MediaStringId.Property.MediaEntity.ITERATION.property { iteration }
            )
    }
}
