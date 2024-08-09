package dev.toastbits.lifelog.core.test

import okio.FileSystem
import okio.Path

interface FileSystemTest {
    val fileSystem: FileSystem

    fun getEmptyTempDir(baseName: String): Path {
        var directory: Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve(baseName)
        try {
            // Some operating systems (Windows) don't like this for some reason
            fileSystem.deleteRecursively(directory)
        }
        catch (_: Throwable) {
            var i: Int = 2
            while (fileSystem.exists(directory)) {
                directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("$baseName-${i++}")
            }
        }
        return directory
    }
}
