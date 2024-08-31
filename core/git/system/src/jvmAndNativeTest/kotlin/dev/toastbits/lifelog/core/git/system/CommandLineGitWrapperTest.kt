package dev.toastbits.lifelog.core.git.system

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

class CommandLineGitWrapperTest: GitWrapperTest() {
    override fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper =
        GitWrapper.createCommandLine(directory, dispatcher)
}
