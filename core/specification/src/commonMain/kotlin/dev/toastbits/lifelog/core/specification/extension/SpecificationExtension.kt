package dev.toastbits.lifelog.core.specification.extension

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

typealias ExtensionId = String

interface SpecificationExtension {
    val id: ExtensionId
    val name: String
    val extraEventTypes: List<LogEventType>
    val extraReferenceTypes: List<LogEntityReferenceType>
}

fun SpecificationExtension.validate() {
    for (type in extraReferenceTypes) {
        check(type.identifier.isNotBlank()) { "Identifier for reference type $type is blank" }
        check(type.identifier.none { LogFileConverterStrings.ILLEGAL_PATH_CHARS.contains(it) }) { "Reference type identifier '${type.identifier}' contains illegal character(s)" }
    }
}
