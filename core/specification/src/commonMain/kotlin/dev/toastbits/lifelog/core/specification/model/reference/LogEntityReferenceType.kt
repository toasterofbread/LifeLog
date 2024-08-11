package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogEntityMetadata
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import kotlinx.datetime.LocalDate

sealed interface LogEntityReferenceType {
    val id: ExtensionId
    val extensionId: ExtensionId

    abstract class InMetadata: LogEntityReferenceType {
        abstract fun parseReference(path: List<String>, onAlert: (LogParseAlert) -> Unit): LogEntityReference.InMetadata?
        abstract fun parseReferenceMetadata(path: List<String>, lines: Sequence<String>, onAlert: (ParseAlertData) -> Unit): LogEntityMetadata?
    }

    abstract class InLog: LogEntityReferenceType {
        abstract fun parseReference(path: List<String>, date: LocalDate, onAlert: (LogParseAlert) -> Unit): LogEntityReference.InLog?
    }
}
