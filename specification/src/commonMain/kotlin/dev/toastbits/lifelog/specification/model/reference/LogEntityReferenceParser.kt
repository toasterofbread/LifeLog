package dev.toastbits.lifelog.specification.model.reference

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert

interface LogEntityReferenceParser {
    fun parseReference(text: String, onAlert: (LogParseAlert) -> Unit): LogEntityReference<*>?
}
