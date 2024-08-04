package dev.toastbits.lifelog.extension.media

import dev.toastbits.lifelog.extension.media.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import dev.toastbits.lifelog.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceType

class MediaExtension: SpecificationExtension {
    override fun getExtraEventTypes(): List<LogEventType<*, *>> =
        listOf(
            MediaConsumeEventTypeImpl()
        )

    override fun getExtraReferenceTypes(): List<LogEntityReferenceType<*>> =
        listOf(
            MediaReferenceType()
        )
}
