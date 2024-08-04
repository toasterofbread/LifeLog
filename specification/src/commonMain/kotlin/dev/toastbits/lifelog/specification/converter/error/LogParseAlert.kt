package dev.toastbits.lifelog.specification.converter.error

sealed interface LogParseAlert {
    val severity: Severity

    enum class Severity {
        WARNING, ERROR
    }

    sealed interface Error: LogParseAlert {
        override val severity: Severity get() = Severity.ERROR
    }

    sealed interface Warning: LogParseAlert {
        override val severity: Severity get() = Severity.WARNING
    }

    data object NoMatchingDateFormat: Error
    data object MissingDateError: Error
    data object UnterminatedEventMetadata: Error
    data object EventContentNotTerminated: Warning
    data class UnhandledMarkdownNodeType(val typeName: String, val scope: String): Warning
    data object UnknownReferenceType: Warning
    data object InvalidReferenceFormat: Warning
}
