package dev.toastbits.lifelog.extension.media.impl

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.DatabaseFileStructureProvider
import dev.toastbits.lifelog.core.accessor.MutableDatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.extension.DatabaseFileStructurePreprocessor
import dev.toastbits.lifelog.core.accessor.walkFiles
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import okio.FileSystem

class GDocsDatabaseFileStructurePreprocessor: DatabaseFileStructurePreprocessor {
    override fun processDatabaseFileStructure(
        fileStructure: DatabaseFileStructure,
        fileStructureProvider: DatabaseFileStructureProvider,
        strings: LogFileConverterStrings,
        extensions: List<SpecificationExtension>,
        onAlert: (ParseAlertData) -> Unit
    ): DatabaseFileStructure {
        val newStructure: MutableDatabaseFileStructure = MutableDatabaseFileStructure()
        val extraEventTypePrefixes: List<String> = extensions.getAllExtraEventTypePrefixes()

        fileStructure.walkFiles { file, path ->
            var newFile: DatabaseFileStructure.Node.File = file

            if (path.segments.size == fileStructureProvider.getLogFilePathSize() && path.name == strings.logFileName && path.segments.firstOrNull() == strings.logsDirectoryName) {
                newFile = object : DatabaseFileStructure.Node.File {
                    override suspend fun readLines(fileSystem: FileSystem): Sequence<String> {
                        return strings.processLogFile(file.readLines(fileSystem), extraEventTypePrefixes)
                    }
                }
            }

            newStructure.createFile(path, newFile)
        }

        return newStructure
    }

    private fun List<SpecificationExtension>.getAllExtraEventTypePrefixes(): List<String> =
        flatMap { extension ->
            extension.extraEventTypes.flatMap { type ->
                type.prefixes
            }
        }

    private fun LogFileConverterStrings.processLogFile(lines: Sequence<String>, extraEventTypePrefixes: List<String>): Sequence<String> = lines.map {
        var line: String = it
        if (line.isBlank() || line.startsWith(contentIndentation)) {
            return@map line
        }

        if (line.startsWith("\\-")) {
            line = line.drop(1)
        }

//        val lowercase: String = line.lowercase()
//        if (!line.startsWith(commentPrefix) && !line.startsWith(datePrefix) && extraEventTypePrefixes.none { prefix -> lowercase.startsWith(prefix) }) {
//            line = missingEventPrefixReplacement + line
//        }

        return@map line
    }
}
