package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.extension.media.MediaExtensionStrings
import kotlin.reflect.KClass

class MediaReferenceType(
    private val strings: MediaExtensionStrings
): LogEntityReferenceType {
    override val id: String get() = strings.mediaReferenceTypeIdentifier
    override val extensionId: ExtensionId get() = strings.extensionId

    override fun parseReference(
        path: List<String>,
        onAlert: (LogParseAlert) -> Unit
    ): LogEntityReference? {
        TODO(path.toString())
    }

    override fun parseReferenceMetadata(
        path: List<String>,
        lines: Sequence<String>,
        onAlert: (ParseAlertData) -> Unit
    ): LogEntityMetadata? {
        throw IllegalStateException()
    }
}
