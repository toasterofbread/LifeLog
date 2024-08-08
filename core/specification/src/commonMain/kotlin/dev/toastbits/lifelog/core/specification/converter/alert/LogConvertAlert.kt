package dev.toastbits.lifelog.core.specification.converter.alert

sealed interface LogConvertAlert {
    val severity: Severity

    enum class Severity {
        WARNING, ERROR
    }
}