package dev.toastbits.lifelog.extension.media.impl

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.DatabaseFileStructureProvider
import dev.toastbits.lifelog.core.accessor.MutableDatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.extension.DatabaseFileStructurePreprocessor
import dev.toastbits.lifelog.core.accessor.walkFiles
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogParseAlert
import dev.toastbits.lifelog.core.specification.converter.alert.SpecificationLogParseAlert
import dev.toastbits.lifelog.core.specification.extension.SpecificationExtension
import dev.toastbits.lifelog.core.specification.impl.converter.DateLineParser
import dev.toastbits.lifelog.core.specification.model.reference.LogEntityReference
import dev.toastbits.lifelog.extension.media.GDocsExtensionStrings
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.alert.GDocsLogParseAlert
import dev.toastbits.lifelog.extension.media.model.ImageData
import dev.toastbits.lifelog.extension.media.model.reference.MediaReference
import dev.toastbits.lifelog.extension.media.model.reference.MediaReferenceType
import kotlinx.datetime.LocalDate
import okio.FileSystem
import okio.Path

class GDocsDatabaseFileStructurePreprocessor(
    private val gdocsStrings: GDocsExtensionStrings
): DatabaseFileStructurePreprocessor {
    override suspend fun processDatabaseFileStructure(
        fileStructure: DatabaseFileStructure,
        fileStructureProvider: DatabaseFileStructureProvider,
        fileSystem: FileSystem,
        strings: LogFileConverterStrings,
        extensions: List<SpecificationExtension>,
        onAlert: (ParseAlertData) -> Unit
    ): DatabaseFileStructure {
        val mediaExtension: MediaExtension? = extensions.firstOrNull { it is MediaExtension } as MediaExtension?
        if (mediaExtension == null) {
            onAlert(ParseAlertData(GDocsLogParseAlert.MediaExtensionNotPresent(gdocsStrings.extensionId), null, null))
        }

        val mediaReferenceType: MediaReferenceType? = mediaExtension?.extraInLogReferenceTypes?.firstOrNull { it is MediaReferenceType } as MediaReferenceType?
        if (mediaReferenceType == null) {
            onAlert(ParseAlertData(GDocsLogParseAlert.MediaReferenceTypeNotPresent(gdocsStrings.extensionId), null, null))
        }

        val newStructure: MutableDatabaseFileStructure = MutableDatabaseFileStructure()

        fileStructure.walkFiles { file, path ->
            var newFile: DatabaseFileStructure.Node.File = file

            if (path.segments.size == fileStructureProvider.getLogFilePathSize() && path.name == strings.logFileName && path.segments.firstOrNull() == strings.logsDirectoryName) {
                val newFileLength: Int =
                    preprocessLogFile(file.readLines(fileSystem), newStructure, mediaReferenceType) { alert, line ->
                        onAlert(ParseAlertData(alert, line.toUInt(), path.toString()))
                    }

                newFile = object : DatabaseFileStructure.Node.File {
                    override suspend fun readLines(fileSystem: FileSystem): Sequence<String> =
                        processLogFile(
                            file.readLines(fileSystem).take(newFileLength),
                            fileStructureProvider,
                            mediaReferenceType,
                            strings
                        ) { alert, line ->
                            onAlert(ParseAlertData(alert, line.toUInt(), path.toString()))
                        }
                }
            }

            newStructure.createFile(path, newFile)
        }

        return newStructure
    }

    private fun preprocessLogFile(
        lines: Sequence<String>,
        newStructure: MutableDatabaseFileStructure,
        mediaReferenceType: MediaReferenceType?,
        onAlert: (LogParseAlert, Int) -> Unit
    ): Int {
        val images: MutableMap<Int, ImageData> = mutableMapOf()
        var lastNonImageLineIndex: Int = -1

        for ((index, line) in lines.withIndex()) {
            if (line.isBlank()) {
                if (images.isEmpty()) {
                    lastNonImageLineIndex = index
                }
                continue
            }

            val parseResult: Pair<Int, ImageData>? = parseImageDataLine(line) { onAlert(it, index) }
            if (parseResult == null) {
                lastNonImageLineIndex = index
                images.clear()
                continue
            }

            images[parseResult.first] = parseResult.second
        }

//        TODO("${images.size} ${lastNonImageLineIndex + 1}")

        return lastNonImageLineIndex + 1
    }

    private fun processLogFile(
        lines: Sequence<String>,
        fileStructureProvider: DatabaseFileStructureProvider,
        mediaReferenceType: MediaReferenceType?,
        strings: LogFileConverterStrings,
        onAlert: (LogParseAlert, Int) -> Unit
    ): Sequence<String> = sequence {
        var currentDate: LocalDate? = null
        var currentLine: Int = 0

        val dateLineParser: DateLineParser =
            object : DateLineParser(strings) {
                override fun onAlert(alert: LogParseAlert) {
                    onAlert(alert, currentLine)
                }
            }

        for (_line in lines) {
            currentLine++

            var line: String = _line.replace("\\", "")

//            if (line.startsWith("\\-")) {
//                line = line.drop(1)
//            }

            val dateLine: DateLineParser.DateLineData? = dateLineParser.attemptParseDateLine(line)
            if (dateLine != null) {
                currentDate = dateLine.date
                yield(line)
                continue
            }

            if (currentDate == null) {
                onAlert(SpecificationLogParseAlert.LogEventOutsideDay, currentLine)
                yield(line)
            }
            else {
                yield(line.replaceLocalImageLinks(currentDate, fileStructureProvider, mediaReferenceType))
            }
        }
    }

    private fun String.replaceLocalImageLinks(
        date: LocalDate,
        fileStructureProvider: DatabaseFileStructureProvider,
        mediaReferenceType: MediaReferenceType?
    ): String {
        var string: String = this

        while (true) {
            val linkStart: Int = string.indexOf("![][image")
            if (linkStart == -1) {
                break
            }
            val linkEnd: Int = string.indexOf(']', linkStart + 10)
            if (linkEnd == -1) {
                break
            }

            val imageIndex: UInt = string.substring(linkStart + 9, linkEnd).toUIntOrNull() ?: break

            val replacement: String =
                if (mediaReferenceType != null) {
                    val reference: LogEntityReference =
                        MediaReference(
                            index = imageIndex,
                            type = MediaReference.Type.IMAGE,
                            logDate = date
                        )

                    val referencePath: Path = fileStructureProvider.getEntityReferenceFilePath(reference)
                    "![][$referencePath]"
                }
                else ""

            string = string.replaceRange(linkStart, linkEnd, replacement)
        }

        return string
    }
}
