package dev.toastbits.lifelog.extension.media

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.extension.media.impl.MediaExtensionStringsImpl
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType

class MediaExtension(
    override val id: ExtensionId = "media",
    val strings: MediaExtensionStrings = MediaExtensionStringsImpl(),
    mediaReferenceType: MediaReferenceType = MediaReferenceType(strings, id)
): SpecificationExtension {
    override val name: String get() = strings.extensionIdentifier

    override val extraEventTypes: List<LogEventType> = emptyList()

    override val extraReferenceTypes: List<LogEntityReferenceType> =
        listOf(
            mediaReferenceType
        )
}
