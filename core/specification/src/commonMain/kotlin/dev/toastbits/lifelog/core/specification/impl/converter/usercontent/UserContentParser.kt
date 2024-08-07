package dev.toastbits.lifelog.core.specification.impl.converter.usercontent

import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser

interface UserContentParser {
    fun parseUserContent(
        markdownText: String,
        referenceParser: LogEntityReferenceParser,
        onAlert: (alert: LogParseAlert, line: Int) -> Unit
    ): UserContent
}
