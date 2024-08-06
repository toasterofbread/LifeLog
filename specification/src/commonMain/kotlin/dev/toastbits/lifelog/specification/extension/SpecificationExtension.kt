package dev.toastbits.lifelog.specification.extension

import dev.toastbits.lifelog.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceType

interface SpecificationExtension {
    fun getExtraEventTypes(): List<LogEventType<*>> = emptyList()
    fun getExtraReferenceTypes(): List<LogEntityReferenceType<*>> = emptyList()
}
