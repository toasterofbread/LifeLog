package dev.toastbits.lifelog.specification.model.reference

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.util.StringId

interface LogEntityReferenceType<T: LogEntityReference<*>> {
    val name: StringId
    val prefixes: List<String>

    fun parseReference(reference: String, prefixIndex: Int, onAlert: (LogParseAlert) -> Unit): T?
}
