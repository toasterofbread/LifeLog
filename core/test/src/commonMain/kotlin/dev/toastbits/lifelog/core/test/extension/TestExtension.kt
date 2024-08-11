package dev.toastbits.lifelog.core.test.extension

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceType

object TestExtension: SpecificationExtension {
    override val id: ExtensionId = "test"
    override val extraEventTypes: List<LogEventType> = listOf(TestLogEventType)
    override val extraReferenceTypes: List<LogEntityReferenceType> = listOf(TestLogEntityReferenceType)
}
