package dev.toastbits.lifelog.core.specification.converter.alert

import dev.toastbits.lifelog.core.specification.converter.alert.LogConvertAlert.Severity
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference

sealed interface LogParseAlert: LogConvertAlert {
    private interface Error: LogParseAlert {
        override val severity: Severity get() = Severity.ERROR
    }

    private interface Warning: LogParseAlert {
        override val severity: Severity get() = Severity.WARNING
    }

    data class NoMatchingDateFormat(val dateText: String): Error
    data object MissingDateError: Error
    data object UnterminatedEventMetadata: Error
    data object EventContentNotTerminated: Warning
    data class UnhandledMarkdownNodeType(val typeName: String, val startIndex: Int, val endIndex: Int, val scope: String, val text: String): Warning
    data class UnmatchedEventFormat(val text: String, val availablePrefixes: List<String>): Warning

    data class InvalidReferenceFormat(val referenceText: String): Warning
    data class UnknownReferenceType(val referencePath: List<String>, val firstUnknownSegment: Int): Warning
    data class InvalidReferenceSize(val referencePath: List<String>, val expectedSize: Int): Warning

    data class RedefinedMetadataValue(val key: LogEntityReference): Warning

    data class UnknownIterationSpecifier(val text: String): Warning
    data class InvalidEpisodesSpecifier(val text: String): Warning

    data class UnregisteredExtension(val extensionId: ExtensionId): Warning
    data class UnregisteredReferenceType(val referenceTypeId: ExtensionId, val extensionId: ExtensionId): Warning

    data object LogEventOutsideDay: Warning
}
