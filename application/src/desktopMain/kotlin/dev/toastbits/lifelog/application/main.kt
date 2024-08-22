package dev.toastbits.lifelog.application

import androidx.compose.ui.window.singleWindowApplication
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

fun main() {
    runBlocking {
        gitTest()
    }
    return

    val application: Application = Application()
    singleWindowApplication {
        application.Main()
    }

    application.onClose()
}

private fun test() = runBlocking {
    println("---START---")

    val remote: GitRemoteBranch = GitRemoteBranch("origin", "https://github.com/toasterofbread/CONSUME", "gdocs-import")

    val helper: DatabaseHelper =
        DatabaseHelper(Dispatchers.IO, LogFileSplitStrategy.Year).apply {
            registerExtension(MediaWatchExtension())
            registerExtension(MediaExtension())
            registerExtension(GDocsExtension())
        }

    val inAccessor: GitLogDatabaseAccessor =
        helper.createAccessor(DesktopJvmGitWrapper("/home/toaster/Downloads/test".toPath(), Dispatchers.IO))

    val database: LogDatabase = inAccessor.loadDatabaseLocally { TODO(it.toString()) }

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

    println("Saving...")
    outAccessor.saveDatabaseLocally(database) { TODO(it.toString()) }
//    outAccessor.saveDatabaseRemotely(database, "Subsequent commit") { TODO(it.toString()) }

    println("---END---")
}
