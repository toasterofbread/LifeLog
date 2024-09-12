package dev.toastbits.lifelog.core.specification.model.reference

import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
sealed interface LogEntityReference {
    val extensionId: ExtensionId?
    val referenceTypeId: ExtensionId?
    val path: LogEntityPath

    interface InLog: LogEntityReference {
        val logDate: LocalDate
    }
    interface InMetadata: LogEntityReference

    @Serializable
    data class InLogData(
        override val logDate: LocalDate,
        override val path: LogEntityPath,
        override val extensionId: ExtensionId?,
        override val referenceTypeId: ExtensionId?
    ) : InLog

    @Serializable
    data class InMetadataData(
        override val path: LogEntityPath,
        override val extensionId: ExtensionId,
        override val referenceTypeId: ExtensionId
    ) : InMetadata

    // TODO | Should probably be separate
    @Serializable
    data class URL(val url: String): LogEntityReference {
        override val extensionId: ExtensionId? = null
        override val referenceTypeId: ExtensionId? = null
        override val path: LogEntityPath get() = throw NotImplementedError()
    }
}

@Serializable
data class LogEntityPath(val segments: List<String>) {
    override fun toString(): String = segments.joinToString("/")

    companion object {
        val ROOT: LogEntityPath get() = of("/")

        fun of(vararg segments: String): LogEntityPath =
            LogEntityPath(segments.toList())
    }
}
