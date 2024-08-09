package dev.toastbits.lifelog.core.saver.reference

import dev.toastbits.lifelog.core.saver.LogDatabaseFileStructureProvider
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser

class LogEntityReferenceParserImpl(
    private val fileStructureProvider: LogDatabaseFileStructureProvider
): LogEntityReferenceParser {
    override fun parseReference(
        text: String,
        onAlert: (LogParseAlert) -> Unit
    ): LogEntityReference? {
        // TODO
        return null
//        for (referenceType in referenceTypes) {
//            for ((index, prefix) in referenceType.prefixes.withIndex()) {
//                if (prefix.length > text.length) {
//                    continue
//                }
//
//                if (text.take(prefix.length).lowercase() != prefix.lowercase()) {
//                    continue
//                }
//
//                return referenceType.parseReference(text.drop(prefix.length).trimStart(), index, onAlert = onAlert)
//            }
//        }
//
//        onAlert(LogParseAlert.UnknownReferenceType)
//        return null
    }
}
