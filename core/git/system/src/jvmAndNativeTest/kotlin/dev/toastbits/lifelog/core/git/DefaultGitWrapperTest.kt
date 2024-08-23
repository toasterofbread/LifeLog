package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

class DefaultGitWrapperTest: GitWrapperTest() {
    override fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper =
        GitWrapper.createSystemDefault(directory, dispatcher)
}
