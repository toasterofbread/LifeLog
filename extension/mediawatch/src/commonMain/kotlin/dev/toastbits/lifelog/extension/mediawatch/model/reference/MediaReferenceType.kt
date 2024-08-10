package dev.toastbits.lifelog.extension.mediawatch.model.reference

import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType
import dev.toastbits.lifelog.extension.mediawatch.converter.MediaExtensionConverterFormats
import dev.toastbits.lifelog.extension.mediawatch.impl.converter.MediaExtensionConverterFormatsImpl
import kotlin.reflect.KClass

class MediaReferenceType(
    private val converterFormats: MediaExtensionConverterFormats = MediaExtensionConverterFormatsImpl(),
): LogEntityReferenceType {
    override val identifier: String get() = converterFormats.mediaReferenceTypeIdentifier
    override val referenceClass: KClass<*> = MediaReference::class
}