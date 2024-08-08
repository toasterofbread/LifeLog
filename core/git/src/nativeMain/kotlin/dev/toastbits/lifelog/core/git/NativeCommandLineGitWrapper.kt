package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path

class NativeCommandLineGitWrapper(
    private val gitBinaryPath: String,
    override val directory: Path,
    private val dispatcher: CoroutineDispatcher
): CommandLineGitWrapper() {
    override suspend fun runGitCommand(vararg args: String?) = withContext(dispatcher) {
        if (!FileSystem.SYSTEM.exists(directory)) {
            FileSystem.SYSTEM.createDirectories(directory, mustCreate = true)
        }

        val finalArgs: List<String> = listOf("-C", directory.toString()) + args.filterNotNull()
        val result: Int = runCommand(gitBinaryPath, finalArgs)
        check(result == 0) { "Result $result when running $gitBinaryPath $finalArgs" }
    }
}