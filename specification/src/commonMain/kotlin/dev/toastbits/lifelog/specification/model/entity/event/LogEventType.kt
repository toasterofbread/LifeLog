package dev.toastbits.lifelog.specification.model.entity.event

import dev.toastbits.lifelog.specification.model.UserContent
import dev.toastbits.lifelog.specification.util.StringId

interface LogEventType<T: LogEvent, S: StringId> {
    val name: S
    val prefixes: List<String>

    fun parseEvent(prefixIndex: Int, body: String, metadata: String?, content: UserContent): T
}
