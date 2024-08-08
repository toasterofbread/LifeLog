package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

@Throws(GitWrapperCreationException::class)
internal actual fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper =
    DesktopJvmGitWrapper(directory, dispatcher)
