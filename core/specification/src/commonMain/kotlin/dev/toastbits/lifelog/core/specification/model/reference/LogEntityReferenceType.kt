package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.util.StringId

interface LogEntityReferenceType<T: LogEntityReference<*>> {
    val name: StringId
    val prefixes: List<String>

    fun parseReference(reference: String, prefixIndex: Int, onAlert: (dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert) -> Unit): T?
}
