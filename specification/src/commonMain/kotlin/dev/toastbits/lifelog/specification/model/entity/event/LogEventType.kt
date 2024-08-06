package dev.toastbits.lifelog.specification.model.entity.event

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.util.StringId

interface LogEventType<T: LogEvent> {
    val name: StringId
    val prefixes: List<String>

    fun parseEvent(
        prefixIndex: Int,
        body: String,
        metadata: String?,
        content: UserContent,
        onAlert: (LogParseAlert) -> Unit
    ): T
}
