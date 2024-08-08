package dev.toastbits.lifelog.core.specification.impl.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

class LogEntityReferenceGeneratorImpl(
    private val eventTypes: List<LogEventType>,
    private val referenceTypes: List<LogEntityReferenceType>
): LogEntityReferenceGenerator {
    override fun generateReference(
        reference: LogEntityReference,
        onAlert: (LogGenerateAlert) -> Unit
    ): String {
        TODO("Not yet implemented")
    }
}
