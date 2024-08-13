package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import okio.Path

@Throws(GitWrapperCreationException::class)
internal actual fun createSystemDefaultGitWrapper(
    directory: Path,
    ioDispatcher: CoroutineDispatcher,
    fileSystem: FileSystem
): GitWrapper =
    DesktopJvmGitWrapper(directory, ioDispatcher)
