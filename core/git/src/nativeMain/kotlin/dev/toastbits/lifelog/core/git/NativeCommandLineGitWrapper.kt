package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path

class NativeCommandLineGitWrapper(
    private val gitBinaryPath: String,
    override val directory: Path,
    private val ioDispatcher: CoroutineDispatcher
): CommandLineGitWrapper() {
    override suspend fun runGitCommand(vararg args: String?): String = withContext(ioDispatcher) {
        if (!FileSystem.SYSTEM.exists(directory)) {
            FileSystem.SYSTEM.createDirectories(directory, mustCreate = true)
        }

        val finalArgs: Array<String> = arrayOf("-C", directory.toString()) + args.filterNotNull()
        val output: String? = runCommand(gitBinaryPath, *finalArgs)
        checkNotNull(output) { "Failure when running $gitBinaryPath $finalArgs" }

        return@withContext output
    }
}