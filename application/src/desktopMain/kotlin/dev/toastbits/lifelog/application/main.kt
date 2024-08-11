package dev.toastbits.lifelog.application

import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.accessor.impl.git.GitLogDatabaseAccessor
import dev.toastbits.lifelog.core.git.DesktopJvmGitWrapper
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.extension.media.GDocsExtension
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.media.impl.GDocsDatabaseFileStructurePreprocessor
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtension
import dev.toastbits.lifelog.extension.mediawatch.util.MediaEntityType
import dev.toastbits.lifelog.helper.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

fun main() = runBlocking {
    println("---START---")

    val helper: DatabaseHelper =
        DatabaseHelper(Dispatchers.IO, LogFileSplitStrategy.Month).apply {
            registerExtension(MediaWatchExtension())
            registerExtension(MediaExtension())
            registerExtension(GDocsExtension())
        }

    val accessor: GitLogDatabaseAccessor =
        helper.createAccessor(DesktopJvmGitWrapper("/home/toaster/Downloads/test".toPath(), Dispatchers.IO))

    val database: LogDatabase = accessor.loadDatabaseLocally { TODO(it.toString()) }

    println(database)

    println("---END---")
}
