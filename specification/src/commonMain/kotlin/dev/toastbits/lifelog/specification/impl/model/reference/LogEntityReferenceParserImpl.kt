package dev.toastbits.lifelog.specification.impl.model.reference

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceType

class LogEntityReferenceParserImpl(
    private val eventTypes: List<LogEventType<*, *>>,
    private val referenceTypes: List<LogEntityReferenceType<*>>
): LogEntityReferenceParser {
    override fun parseReference(
        text: String,
        onAlert: (LogParseAlert) -> Unit
    ): LogEntityReference<*>? {
        for (referenceType in referenceTypes) {
            for ((index, prefix) in referenceType.prefixes.withIndex()) {
                if (prefix.length > text.length) {
                    continue
                }

                if (text.take(prefix.length).lowercase() != prefix.lowercase()) {
                    continue
                }

                return referenceType.parseReference(text.drop(prefix.length).trimStart(), index, onAlert = onAlert)
            }
        }

        onAlert(LogParseAlert.UnknownReferenceType)
        return null
    }
}
