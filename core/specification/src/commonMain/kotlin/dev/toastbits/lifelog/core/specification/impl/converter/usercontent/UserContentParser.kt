package dev.toastbits.lifelog.core.specification.impl.converter.usercontent

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReferenceParser

interface UserContentParser {
    fun parseUserContent(
        text: String,
        referenceParser: LogEntityReferenceParser,
        onAlert: (alert: LogParseAlert, line: Int) -> Unit
    ): UserContent
}
