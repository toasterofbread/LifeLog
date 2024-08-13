package dev.toastbits.lifelog.application

import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.accessor.impl.git.GitLogDatabaseAccessor
import dev.toastbits.lifelog.core.accessor.model.GitRemoteBranch
import dev.toastbits.lifelog.core.git.DesktopJvmGitWrapper
import dev.toastbits.lifelog.core.git.GitWrapper
import dev.toastbits.lifelog.core.git.util.getLocalGitCredentials
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.extension.media.GDocsExtension
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtension
import dev.toastbits.lifelog.helper.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

fun main() = runBlocking {
    println("---START---")

    val remote: GitRemoteBranch = GitRemoteBranch("origin", "https://github.com/toasterofbread/CONSUME", "gdocs-import")

    val helper: DatabaseHelper =
        DatabaseHelper(Dispatchers.IO, LogFileSplitStrategy.Month).apply {
            registerExtension(MediaWatchExtension())
            registerExtension(MediaExtension())
            registerExtension(GDocsExtension())
        }

    val inAccessor: GitLogDatabaseAccessor =
        helper.createAccessor(DesktopJvmGitWrapper("/home/toaster/Downloads/test".toPath(), Dispatchers.IO))

    val database: LogDatabase = inAccessor.loadDatabaseLocally { TODO(it.toString()) }
    println(database)

    val outAccessor: GitLogDatabaseAccessor =
        helper.createAccessor(
            DesktopJvmGitWrapper(
                "/home/toaster/Downloads/test/out".toPath(),
                Dispatchers.IO
            ),
            remote = remote
        )

    val credentials: GitWrapper.Credentials? = getLocalGitCredentials(remote.remoteUrl, inAccessor.directory.toString())
    checkNotNull(credentials)

    outAccessor.setCredentials(credentials)
    outAccessor.saveDatabaseRemotely(database, "Initial commit") { TODO(it.toString()) }

    println("---END---")
}
