package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.extension.media.MediaExtensionStrings
import kotlin.reflect.KClass

class MediaReferenceType(
    private val strings: MediaExtensionStrings,
    override val extensionId: ExtensionId
): LogEntityReferenceType {
    override val identifier: String get() = strings.mediaReferenceTypeIdentifier

    override fun parseReference(
        path: List<String>,
        onAlert: (LogParseAlert) -> Unit
    ): LogEntityReference? {
        TODO(path.toString())
    }
}
