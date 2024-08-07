package dev.toastbits.lifelog.extension.media

import dev.toastbits.lifelog.extension.media.converter.MediaExtensionConverterFormats
import dev.toastbits.lifelog.extension.media.impl.converter.MediaExtensionConverterFormatsImpl
import dev.toastbits.lifelog.extension.media.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

class MediaExtension(
    val converterFormats: MediaExtensionConverterFormats = MediaExtensionConverterFormatsImpl()
): SpecificationExtension {
    override fun getExtraEventTypes(): List<LogEventType> =
        listOf(
            MediaConsumeEventTypeImpl(converterFormats)
        )

    override fun getExtraReferenceTypes(): List<LogEntityReferenceType> =
        listOf(
            MediaReferenceType()
        )
}
