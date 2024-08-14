package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

sealed interface MediaRangeValue {
    data class Discrete(val value: UInt): MediaRangeValue
    data class Continuous(val value: Float): MediaRangeValue

    companion object {
        fun fromString(string: String): MediaRangeValue? =
            string.toUIntOrNull()?.let { Discrete(it) } ?: string.toFloatOrNull()?.let { Continuous(it) }
    }
}

val UInt.mediaRangeValue: MediaRangeValue get() =
    MediaRangeValue.Discrete(this)
