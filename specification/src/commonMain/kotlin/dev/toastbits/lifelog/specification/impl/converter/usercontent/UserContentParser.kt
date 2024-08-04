package dev.toastbits.lifelog.specification.impl.converter.usercontent

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.model.reference.LogEntityReferenceParser

interface UserContentParser {
    fun parseUserContent(
        markdownText: String,
        referenceParser: LogEntityReferenceParser,
        onAlert: (LogParseAlert) -> Unit
    ): UserContent
}
