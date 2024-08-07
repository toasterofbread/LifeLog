package dev.toastbits.lifelog.core.specification.model.entity

import dev.toastbits.lifelog.core.specification.util.Language

interface LogEntityPropertySuggestionProvider<T> {
    suspend fun getSuggestions(currentValue: T?): List<T>
    fun getSuggestionDisplay(suggestion: T, language: Language): String
}
