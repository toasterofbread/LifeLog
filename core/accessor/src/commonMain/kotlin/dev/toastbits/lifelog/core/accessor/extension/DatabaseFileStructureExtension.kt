package dev.toastbits.lifelog.core.accessor.extension

import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension

interface DatabaseFileStructureExtension: SpecificationExtension {
    val extraPreprocessors: List<DatabaseFileStructurePreprocessor> get() = emptyList()
}
