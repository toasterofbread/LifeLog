package dev.toastbits.lifelog.extension.mediawatch.impl.model.entity.event

sealed interface MediaRangeValue {
    operator fun unaryMinus(): MediaRangeValue

    data class Discrete(val value: Int): MediaRangeValue {
        override fun unaryMinus(): MediaRangeValue = Discrete(-value)
    }
    data class Continuous(val value: Float): MediaRangeValue {
        override fun unaryMinus(): MediaRangeValue = Continuous(-value)
    }

    companion object {
        fun fromString(string: String): MediaRangeValue? =
            string.toIntOrNull()?.let { Discrete(it) } ?: string.toFloatOrNull()?.let { Continuous(it) }
    }
}

val Int.mediaRangeValue: MediaRangeValue get() =
    MediaRangeValue.Discrete(this)

val Float.mediaRangeValue: MediaRangeValue get() =
    MediaRangeValue.Continuous(this)
