package dev.toastbits.lifelog.extension.mediawatch

import dev.toastbits.lifelog.extension.mediawatch.converter.MediaExtensionConverterFormats
import dev.toastbits.lifelog.extension.mediawatch.impl.converter.MediaExtensionConverterFormatsImpl
import dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event.MediaConsumeEventTypeImpl
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.MediaConsumeEventType
import dev.toastbits.lifelog.extension.mediawatch.model.reference.MediaReferenceType

class MediaWatchExtension(
    val converterFormats: MediaExtensionConverterFormats = MediaExtensionConverterFormatsImpl(),
    mediaConsumeEventType: MediaConsumeEventType = MediaConsumeEventTypeImpl(converterFormats),
    mediaReferenceType: MediaReferenceType = MediaReferenceType()
): SpecificationExtension {
    override val identifier: String get() = converterFormats.extensionIdentifier

    override val extraEventTypes: List<LogEventType> =
        listOf(
            mediaConsumeEventType
        )

    override val extraReferenceTypes: List<LogEntityReferenceType> =
        listOf(
            mediaReferenceType
        )
}
