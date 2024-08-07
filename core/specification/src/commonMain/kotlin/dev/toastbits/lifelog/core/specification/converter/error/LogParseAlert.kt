package dev.toastbits.lifelog.core.specification.converter.error

sealed interface LogParseAlert {
    val severity: dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Severity

    enum class Severity {
        WARNING, ERROR
    }

    sealed interface Error: dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert {
        override val severity: dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Severity get() = dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Severity.ERROR
    }

    sealed interface Warning:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert {
        override val severity: dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Severity get() = dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Severity.WARNING
    }

    data object NoMatchingDateFormat:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Error
    data object MissingDateError:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Error
    data object UnterminatedEventMetadata:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Error
    data object EventContentNotTerminated:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
    data class UnhandledMarkdownNodeType(val typeName: String, val startIndex: Int, val endIndex: Int, val scope: String, val text: String):
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
    data object UnknownReferenceType:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
    data class UnmatchedEventFormat(val text: String):
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
    data object InvalidReferenceFormat:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
    data object InvalidReferenceSize:
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Error

    data class UnknownIterationSpecifier(val text: String):
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
    data class InvalidEpisodesSpecifier(val text: String):
        dev.toastbits.lifelog.core.specification.converter.error.LogParseAlert.Warning
}
