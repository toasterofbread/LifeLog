package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

@Throws(GitWrapperCreationException::class)
internal actual fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper {
    val binaryPath: String = getGitBinaryPath()
    try {
        val result: Int = runCommand(binaryPath, listOf("--version"))
        check(result == 0)
    }
    catch (_: Throwable) {
        throw GitWrapperCreationException.GitBinaryNotFunctional(binaryPath)
    }
    return NativeCommandLineGitWrapper(binaryPath, directory, dispatcher)
}
