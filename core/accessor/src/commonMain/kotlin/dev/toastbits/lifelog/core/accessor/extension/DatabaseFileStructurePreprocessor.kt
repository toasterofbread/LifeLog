package dev.toastbits.lifelog.core.accessor.extension

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructureProvider
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import okio.FileSystem

interface DatabaseFileStructurePreprocessor {
    suspend fun processDatabaseFileStructure(
        fileStructure: FileStructure,
        fileStructureProvider: DatabaseFileStructureProvider,
        strings: LogFileConverterStrings,
        extensions: List<SpecificationExtension>,
        onAlert: (ParseAlertData) -> Unit
    ): FileStructure
}
