package dev.toastbits.lifelog.core.specification.converter

import dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension

interface LogDatabaseConverter {
    fun registerExtension(specificationExtension: dev.toastbits.lifelog.core.specification.extension.SpecificationExtension)
    fun parseLogDatabase(lines: Iterable<String>): dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter.ParseResult

    interface ParseResult {
        val database: dev.toastbits.lifelog.core.specification.database.LogDatabase
        val alerts: List<dev.toastbits.lifelog.core.specification.converter.LogDatabaseConverter.ParseAlert>
    }

    interface ParseAlert {
        val alert: dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert
        val lineIndex: Int
    }
}
