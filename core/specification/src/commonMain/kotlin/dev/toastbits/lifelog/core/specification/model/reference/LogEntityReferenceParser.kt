package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert

interface LogEntityReferenceParser {
    fun parseReference(text: String, onAlert: (LogParseAlert) -> Unit): LogEntityReference?
}
