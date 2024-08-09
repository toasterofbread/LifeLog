package dev.toastbits.lifelog.core.specification.model.reference

import kotlin.reflect.KClass

interface LogEntityReference {
    val entityTypeClass: KClass<*>
    val entityPath: LogEntityPath
}

class LogEntityPath(val segments: List<String>) {
    override fun toString(): String = segments.joinToString("/")

    companion object {
        val ROOT: LogEntityPath get() = of("/")

        fun of(vararg segments: String): LogEntityPath =
            LogEntityPath(segments.toList())
    }
}
