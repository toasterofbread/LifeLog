package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import kotlinx.datetime.LocalDate

sealed interface LogEntityReference {
    interface InLog: LogEntityReference {
        val logDate: LocalDate
        val path: LogEntityPath
    }
    interface InMetadata: LogEntityReference {
        val path: LogEntityPath
        val extensionId: ExtensionId
        val referenceTypeId: ExtensionId
    }

    data class InLogData(
        override val logDate: LocalDate,
        override val path: LogEntityPath
    ) : InLog
    data class InMetadataData(
        override val path: LogEntityPath,
        override val extensionId: ExtensionId,
        override val referenceTypeId: ExtensionId
    ) : InMetadata
}

data class LogEntityPath(val segments: List<String>) {
    override fun toString(): String = segments.joinToString("/")

    companion object {
        val ROOT: LogEntityPath get() = of("/")

        fun of(vararg segments: String): LogEntityPath =
            LogEntityPath(segments.toList())
    }
}
