package dev.toastbits.lifelog.core.specification.database

import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent

interface LogDatabase {
    val days: Map<LogDate?, List<LogEvent>>
}
