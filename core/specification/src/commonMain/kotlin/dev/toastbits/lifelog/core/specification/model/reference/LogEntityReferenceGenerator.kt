package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert

interface LogEntityReferenceGenerator {
    fun generateReferencePath(
        reference: LogEntityReference,
        relativeToOverride: LogEntityPath? = null,
        onAlert: (LogGenerateAlert) -> Unit
    ): LogEntityPath?
}
