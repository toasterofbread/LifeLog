package dev.toastbits.lifelog.core.specification.extension

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

typealias ExtensionId = String

interface SpecificationExtension {
    val id: ExtensionId

    val extraEventTypes: List<LogEventType> get() = emptyList()
    val extraReferenceTypes: List<LogEntityReferenceType> get() = emptyList()
}

fun SpecificationExtension.validate() {
    for (type in extraReferenceTypes) {
        check(type.id.isNotBlank()) { "Identifier for reference type $type is blank" }
        check(type.id.none { LogFileConverterStrings.ILLEGAL_PATH_CHARS.contains(it) }) { "Reference type identifier '${type.id}' contains illegal character(s)" }
    }
}
