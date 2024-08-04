package dev.toastbits.lifelog.specification.database

import dev.toastbits.lifelog.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.specification.model.entity.event.LogEvent

interface LogDatabase {
    val days: Map<LogDate?, List<LogEvent>>
}
