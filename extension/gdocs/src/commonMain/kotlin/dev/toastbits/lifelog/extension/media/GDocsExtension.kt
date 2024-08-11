package dev.toastbits.lifelog.extension.media

import dev.toastbits.lifelog.core.accessor.extension.DatabaseFileStructureExtension
import dev.toastbits.lifelog.core.accessor.extension.DatabaseFileStructurePreprocessor
import dev.toastbits.lifelog.core.specification.extension.ExtensionId
import dev.toastbits.lifelog.extension.media.impl.GDocsDatabaseFileStructurePreprocessor
import dev.toastbits.lifelog.extension.media.impl.GDocsExtensionStringsImpl

class GDocsExtension(
    val strings: GDocsExtensionStrings = GDocsExtensionStringsImpl(),
    preprocessor: GDocsDatabaseFileStructurePreprocessor = GDocsDatabaseFileStructurePreprocessor()
): DatabaseFileStructureExtension {
    override val id: ExtensionId get() = strings.extensionId

    override val extraPreprocessors: List<DatabaseFileStructurePreprocessor> =
        listOf(preprocessor)
}
