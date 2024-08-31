package dev.toastbits.lifelog.core.git.system

import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import okio.Path

@Throws(GitWrapperCreationException::class)
internal actual fun createSystemDefaultGitWrapper(
    directory: Path,
    ioDispatcher: CoroutineDispatcher,
    fileSystem: FileSystem
): GitWrapper =
    CommandLineGitWrapper(getAndCheckGitBinaryPath(), directory, fileSystem, ioDispatcher)
