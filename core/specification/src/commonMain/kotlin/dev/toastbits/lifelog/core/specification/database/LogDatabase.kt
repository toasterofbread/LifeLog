package dev.toastbits.lifelog.core.specification.database

import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference

data class LogDatabase(
    val days: Map<LogDate, List<LogEvent>> = emptyMap(),
    val data: Map<LogEntityReference, LogDataFile> = emptyMap()
)

sealed interface LogDataFile {
    class Lines(val lines: List<String>): LogDataFile
    class Bytes(val bytes: ByteArray, val range: IntRange = bytes.indices): LogDataFile
}
