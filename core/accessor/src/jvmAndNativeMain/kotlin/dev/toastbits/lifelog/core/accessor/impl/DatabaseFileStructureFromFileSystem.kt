package dev.toastbits.lifelog.core.accessor.impl

import dev.toastbits.lifelog.core.accessor.DatabaseFileStructure
import dev.toastbits.lifelog.core.accessor.MutableDatabaseFileStructure
import okio.FileSystem
import okio.Path

internal fun FileSystem.getDatabaseFileStructure(path: Path): DatabaseFileStructure {
    val fileStructure: MutableDatabaseFileStructure = MutableDatabaseFileStructure()

    for (file in listRecursively(path)) {
        if (metadataOrNull(file)?.isRegularFile != true) {
            continue
        }

        val relativeFile: Path = file.relativeTo(path)
        if (relativeFile.segments.firstOrNull() == ".git") {
            continue
        }

        fileStructure.createFile(relativeFile, OkioNodeFile(file))
    }

    return fileStructure
}

private data class OkioNodeFile(private val path: Path): DatabaseFileStructure.Node.File {
    override suspend fun readLines(fileSystem: FileSystem): Sequence<String> = sequence {
        fileSystem.read(path) {
            while (true) {
                val line: String = readUtf8Line() ?: break
                yield(line)
            }
        }
    }
}