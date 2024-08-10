package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

object TextExtension: SpecificationExtension {
    override val identifier: String = "test"
    override val extraEventTypes: List<LogEventType> = listOf(TestLogEventType)
    override val extraReferenceTypes: List<LogEntityReferenceType> = listOf(TestLogEntityReferenceType)
}
