package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.ExtensionId

interface LogEntityReferenceType {
    val identifier: String
    val extensionId: ExtensionId

    fun parseReference(path: List<String>, onAlert: (LogParseAlert) -> Unit): LogEntityReference?
}
