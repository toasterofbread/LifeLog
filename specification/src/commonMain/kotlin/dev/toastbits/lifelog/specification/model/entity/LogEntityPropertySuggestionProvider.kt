package dev.toastbits.lifelog.specification.model.entity

import dev.toastbits.lifelog.specification.util.Language

interface LogEntityPropertySuggestionProvider<T> {
    suspend fun getSuggestions(currentValue: T?): List<T>
    fun getSuggestionDisplay(suggestion: T, language: Language): String
}
