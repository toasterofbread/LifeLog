package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.util.StringId

interface LogEntityReferenceType {
    val name: StringId
//    val prefixes: List<String>
//
//    fun parseReference(reference: String, prefixIndex: Int, onAlert: (LogParseAlert) -> Unit): LogEntityReference?
//
//    fun canGenerateReference(reference: LogEntityReference): Boolean
//    fun generateReference(reference: LogEntityReference): String
}
