package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

@Throws(GitWrapperCreationException::class)
internal actual fun createGitWrapper(directory: Path, ioDispatcher: CoroutineDispatcher): GitWrapper =
    DesktopJvmGitWrapper(directory, ioDispatcher)
