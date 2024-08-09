package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert

interface LogEntityReferenceGenerator {
    fun generateReferencePath(
        reference: LogEntityReference,
        onAlert: (LogGenerateAlert) -> Unit
    ): LogEntityPath?
}
