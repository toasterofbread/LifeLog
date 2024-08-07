package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert

interface LogEntityReferenceParser {
    fun parseReference(text: String, onAlert: (dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert) -> Unit): LogEntityReference<*>?
}
