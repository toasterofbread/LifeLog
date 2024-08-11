package dev.toastbits.lifelog.extension.mediawatch.alert

import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.extension.ExtensionId

sealed interface MediaWatchLogParseAlert: LogParseAlert {
    data class UnknownIterationSpecifier(override val alertExtensionId: ExtensionId, val text: String): MediaWatchLogParseAlert, LogParseAlert.Warning
}
