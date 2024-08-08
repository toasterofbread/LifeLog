package dev.toastbits.lifelog.core.specification.impl.converter.usercontent

import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceGenerator

interface UserContentGenerator {
    fun generateUserContent(
        content: UserContent,
        referenceGenerator: LogEntityReferenceGenerator,
        onAlert: (alert: LogGenerateAlert, line: Int) -> Unit
    ): String
}
