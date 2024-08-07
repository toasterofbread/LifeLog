package dev.toastbits.lifelog.core.specification.converter

import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension

interface LogDatabaseConverter {
    fun registerExtension(specificationExtension: SpecificationExtension)
    fun parseLogDatabase(lines: Iterable<String>): ParseResult

    interface ParseResult {
        val database: LogDatabase
        val alerts: List<ParseAlert>
    }

    interface ParseAlert {
        val alert: LogParseAlert
        val lineIndex: Int
    }
}
