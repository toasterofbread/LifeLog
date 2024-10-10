package dev.toastbits.lifelog.application.settings.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

enum class DisplayDateFormat {
    ISO_8601;

    fun getLocalDateFormat(): DateTimeFormat<LocalDate> =
        when (this) {
            ISO_8601 -> LocalDate.Formats.ISO
        }

    companion object {
        val DEFAULT: DisplayDateFormat = ISO_8601
    }
}
