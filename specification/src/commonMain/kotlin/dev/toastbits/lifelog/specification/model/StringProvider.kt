package dev.toastbits.lifelog.specification.model

import dev.toastbits.lifelog.specification.util.Language
import dev.toastbits.lifelog.specification.util.StringId

interface StringProvider<T: StringId> {
    fun getString(stringId: T, language: Language): String
}
