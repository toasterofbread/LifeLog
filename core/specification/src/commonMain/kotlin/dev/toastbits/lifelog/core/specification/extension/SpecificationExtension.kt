package dev.toastbits.lifelog.core.specification.extension

import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

interface SpecificationExtension {
    fun getExtraEventTypes(): List<LogEventType<*>> = emptyList()
    fun getExtraReferenceTypes(): List<LogEntityReferenceType<*>> = emptyList()
}
