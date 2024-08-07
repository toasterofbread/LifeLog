package dev.toastbits.lifelog.core.specification.model.entity.event

import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.model.UserContent
import dev.toastbits.lifelog.core.specification.util.StringId

interface LogEventType {
    val name: StringId
    val prefixes: List<String>

    fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent?,
        onAlert: (LogParseAlert) -> Unit
    ): LogEvent
}
