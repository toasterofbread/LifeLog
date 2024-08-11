package dev.toastbits.lifelog.core.specification.database

import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference

data class LogDatabase(
    val days: Map<LogDate, List<LogEvent>> = emptyMap(),
    val metadata: Map<LogEntityReference, LogEntityMetadata> = emptyMap()
)

data class LogEntityMetadata(val temp: String)
