package dev.toastbits.lifelog.core.specification.util

interface LogModelStringProvider {
    fun getLogModelString(string: LogStringId, language: Language): String
}
