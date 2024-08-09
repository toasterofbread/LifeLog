package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

@Throws(GitWrapperCreationException::class)
internal actual fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper {
    val binaryPath: String = getGitBinaryPath()
    try {
        checkNotNull(runCommand(binaryPath, "--version"))
    }
    catch (e: Throwable) {
        throw GitWrapperCreationException.GitBinaryNotFunctional(binaryPath)
    }
    return NativeCommandLineGitWrapper(binaryPath, directory, dispatcher)
}
