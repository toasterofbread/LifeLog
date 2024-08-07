package dev.toastbits.lifelog.core.specification.converter.error

sealed interface LogParseAlert {
    val severity: Severity

    enum class Severity {
        WARNING, ERROR
    }

    sealed interface Error: LogParseAlert {
        override val severity: Severity get() = Severity.ERROR
    }

    sealed interface Warning:
        LogParseAlert {
        override val severity: Severity get() = Severity.WARNING
    }

    data object NoMatchingDateFormat:
        Error
    data object MissingDateError:
        Error
    data object UnterminatedEventMetadata:
        Error
    data object EventContentNotTerminated:
        Warning
    data class UnhandledMarkdownNodeType(val typeName: String, val startIndex: Int, val endIndex: Int, val scope: String, val text: String):
        Warning
    data object UnknownReferenceType:
        Warning
    data class UnmatchedEventFormat(val text: String):
        Warning
    data object InvalidReferenceFormat:
        Warning
    data object InvalidReferenceSize:
        Error

    data class UnknownIterationSpecifier(val text: String):
        Warning
    data class InvalidEpisodesSpecifier(val text: String):
        Warning
}
