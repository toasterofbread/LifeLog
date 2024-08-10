package dev.toastbits.lifelog.core.specification.converter

import dev.toastbits.lifelog.core.specification.converter.alert.LogConvertAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogGenerateAlert
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.Extendable
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent

interface LogFileConverter: Extendable {
    fun parseLogFile(lines: Iterable<String>): ParseResult
    fun generateLogFile(days: Map<LogDate, List<LogEvent>>): GenerateResult

    data class ParseResult(
        val days: Map<LogDate, List<LogEvent>>,
        val alerts: List<ParseAlertData>
    )

    data class GenerateResult(
        val lines: List<String>,
        val alerts: List<GenerateAlertData>
    )

    data class AlertOnLine<T: LogConvertAlert>(
        val alert: T,
        val lineIndex: Int
    )
}

typealias ParseAlertData = LogFileConverter.AlertOnLine<LogParseAlert>
typealias GenerateAlertData = LogFileConverter.AlertOnLine<LogGenerateAlert>
