package dev.toastbits.lifelog.core.specification.model

import dev.toastbits.lifelog.core.specification.util.Language
import dev.toastbits.lifelog.core.specification.util.StringId

interface StringProvider<T: StringId> {
    fun getString(stringId: T, language: Language): String
}
