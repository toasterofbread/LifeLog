package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.entity.LogEntity

interface LogEntityReferenceType {
    val id: ExtensionId
    val extensionId: ExtensionId

    fun parseReference(path: List<String>, onAlert: (LogParseAlert) -> Unit): LogEntityReference?

    fun parseReferenceMetadata(path: List<String>, lines: Sequence<String>, onAlert: (ParseAlertData) -> Unit): LogEntityMetadata?
}
