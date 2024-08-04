package dev.toastbits.lifelog.specification.util

interface LogModelStringProvider {
    fun getLogModelString(string: LogStringId, language: Language): String
}
