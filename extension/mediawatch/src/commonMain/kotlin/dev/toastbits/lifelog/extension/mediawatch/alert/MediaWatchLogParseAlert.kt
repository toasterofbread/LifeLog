package dev.toastbits.lifelog.extension.mediawatch.alert

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.mediawatch.model.entity.event.BookMediaConsumeEvent

sealed interface MediaWatchLogParseAlert: LogParseAlert {
    data class UnknownIterationSpecifier(override val alertExtensionId: ExtensionId, val text: String): MediaWatchLogParseAlert, LogParseAlert.Warning
    data class UnknownDurationFormat(override val alertExtensionId: ExtensionId, val text: String): MediaWatchLogParseAlert, LogParseAlert.Warning
    data class IncompatibleBookReadRanges(override val alertExtensionId: ExtensionId?, val initial: BookMediaConsumeEvent.ReadRange, val new: BookMediaConsumeEvent.ReadRange): MediaWatchLogParseAlert, LogParseAlert.Warning
}
