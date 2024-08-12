package dev.toastbits.lifelog.core.specification.impl.converter

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.converter.alert.SpecificationLogParseAlert
import dev.toastbits.lifelog.core.specification.converter.parseOrNull
import dev.toastbits.lifelog.core.specification.model.UserContent
import kotlinx.datetime.LocalDate

abstract class DateLineParser(
    private val strings: LogFileConverterStrings
) {
    data class DateLineData(val date: LocalDate?, val ambiguous: Boolean, val inlineComment: UserContent?)

    abstract fun onAlert(alert: LogParseAlert)

    fun attemptParseDateLine(line: String): DateLineData? {
        if (!line.startsWith(strings.datePrefix)) {
            return null
        }

        var (dateText, inlineComment) = line.drop(strings.datePrefix.length).extractComment()
        var ambiguous: Boolean = false

        if (dateText.lowercase().startsWith(strings.ambiguousDatePrefix.lowercase())) {
            ambiguous = true
            dateText = dateText.drop(strings.ambiguousDatePrefix.length).trimStart()
        }

        val date: LocalDate? = parseDate(dateText)
        return DateLineData(date, ambiguous, inlineComment)
    }

    private fun parseDate(text: String): LocalDate? {
        for (dateFormat in strings.dateFormats) {
            val date: LocalDate = dateFormat.parseOrNull(text) ?: continue
            return date
        }

        onAlert(SpecificationLogParseAlert.NoMatchingDateFormat(text))
        return null
    }

    open fun String.extractComment(): Pair<String, UserContent?> {
        val commentStart: Int = indexOf(strings.commentPrefix)
        if (commentStart == -1) {
            return this.trim() to null
        }
        return substring(0, commentStart).trim() to null
    }
}
