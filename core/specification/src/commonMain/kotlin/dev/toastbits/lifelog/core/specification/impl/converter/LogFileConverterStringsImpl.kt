package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.LogDateFormat
import dev.toastbits.lifelog.core.specification.converter.logDateFormatOf
import dev.toastbits.lifelog.core.specification.converter.customLogDateFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

data class LogFileConverterStringsImpl(
    override val metadataDirectoryName: String = "metadata",
    override val metadataExtensionDirectoryName: String = "extension",
    override val logsDirectoryName: String = "logs",
    override val logFileName: String = "log.md",

    override val contentIndentation: String = "  ",
    override val datePrefix: String = "----- ",
    override val commentPrefix: String = "//",
    override val ambiguousDatePrefix: String = "Since ",

    override val eventMetadataStart: String = "(",
    override val eventMetadataEnd: String = ")",
    override val eventContentStart: String = "{",
    override val eventContentEnd: String = "}",

    override val preferredDateFormat: LogDateFormat = logDateFormatOf(LocalDate.Formats.ISO),
    override val dateFormats: List<LogDateFormat> =
        buildList {
            add(
                // 2024-08-04
                logDateFormatOf(LocalDate.Formats.ISO)
            )

            add(
                // 04 August 2024
                logDateFormatOf {
                    dayOfMonth(Padding.ZERO)
                    char(' ')
                    monthName(MonthNames.ENGLISH_FULL)
                    char(' ')
                    year()
                }
            )
            add(
                // 4 August 2024
                logDateFormatOf {
                    dayOfMonth(Padding.NONE)
                    char(' ')
                    monthName(MonthNames.ENGLISH_FULL)
                    char(' ')
                    year()
                }
            )

            // Summer 2024
            add(SeasonDateFormat(false))
            add(SeasonDateFormat(true))

            add(
                customLogDateFormat(
                    format = { date ->
                        date.year.toString()
                    },
                    parse = { input ->
                        LocalDate(input.toInt(), 1, 1)
                    }
                )
            )

            add(
                // The beginning of time
                customLogDateFormat(
                    format = { date ->
                        check(date == LocalDate.fromEpochDays(0))
                        return@customLogDateFormat "The beginning of time"
                    },
                    parse = { input ->
                        check(input.equals("the beginning of time", ignoreCase = true))
                        return@customLogDateFormat LocalDate.fromEpochDays(0)
                    }
                )
            )
        }
): LogFileConverterStrings {
    override fun numberToIteration(number: Int): String =
        when (number) {
            1 -> "first"
            2 -> "second"
            3 -> "third"
            4 -> "fourth"
            5 -> "fifth"
            6 -> "sixth"
            7 -> "seventh"
            8 -> "eighth"
            9 -> "ninth"
            10 -> "tenth"
            else -> "${number}th"
        }
}

class SeasonDateFormat(private val lowercase: Boolean): LogDateFormat {
    override fun format(date: LocalDate): String {
        var season: String = (SEASONS.entries.firstOrNull { date.monthNumber >= it.key } ?: SEASONS.entries.last()).value.first()
        if (lowercase) {
            season = season.lowercase()
        }
        return "$season ${date.year}"
    }

    override fun parse(input: String): LocalDate {
        val split: List<String> = input.split(" ")
        check(split.size == 2)

        val (seasonName: String, year: String) = split
        val month: Int = SEASONS.entries.first { (_, seasons) ->
            seasons.any { season ->
                (
                    if (lowercase) season.lowercase()
                    else season
                ) == seasonName
            }
        }.key

        return LocalDate(year.toInt(), month, 1)
    }

    companion object {
        private val SEASONS: Map<Int, List<String>> =
            mapOf(3 to listOf("Spring"), 6 to listOf("Summer"), 9 to listOf("Autumn", "Fall"), 12 to listOf("Winter"))
    }
}
