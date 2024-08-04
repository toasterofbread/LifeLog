package dev.toastbits.lifelog.specification.converter

import dev.toastbits.lifelog.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.specification.database.LogDatabase
import dev.toastbits.lifelog.specification.extension.SpecificationExtension

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
