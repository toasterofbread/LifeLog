package dev.toastbits.lifelog.extension.mediawatch.impl.model

import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

interface MediaDurationFormat {
    fun format(duration: Duration): String
    fun parse(input: String): Duration
}

fun MediaDurationFormat.parseOrNull(input: String): Duration? =
    try {
        parse(input)
    }
    catch (_: Throwable) {
        null
    }

fun durationFormatOf(block: DateTimeFormatBuilder.WithTime.() -> Unit): MediaDurationFormat =
    OfDateTimeFormat(LocalTime.Format(block))

fun durationFormatOf(format: DateTimeFormat<LocalTime>): MediaDurationFormat =
    OfDateTimeFormat(format)

fun customMediaDurationFormat(
    format: (Duration) -> String,
    parse: (String) -> Duration
): MediaDurationFormat =
    object : MediaDurationFormat {
        override fun format(duration: Duration): String = format(duration)

        override fun parse(input: String): Duration = parse(input)
    }

private class OfDateTimeFormat(val format: DateTimeFormat<LocalTime>): MediaDurationFormat {
    override fun format(duration: Duration): String = format.format(LocalTime.fromNanosecondOfDay(duration.inWholeNanoseconds))
    override fun parse(input: String): Duration = format.parse(input).toNanosecondOfDay().nanoseconds
}
