package dev.toastbits.lifelog.extension.media.model.reference

import dev.toastbits.lifelog.core.specification.model.reference.LogEntityPath
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import kotlinx.datetime.LocalDate

data class MediaReference(
    val index: UInt,
    val type: Type,
    override val logDate: LocalDate
): LogEntityReference.InLog {
    enum class Type {
        IMAGE
    }

    override val path: LogEntityPath
        get() = LogEntityPath.of(type.name, index.toString())
}
