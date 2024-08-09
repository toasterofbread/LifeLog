package dev.toastbits.lifelog.core.specification.database

import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent

data class LogDatabase(
    val days: Map<LogDate, List<LogEvent>>
)