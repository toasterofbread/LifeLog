package dev.toastbits.lifelog.core.git.system

import dev.toastbits.lifelog.core.git.system.util.getGitBinaryPath
import dev.toastbits.lifelog.core.git.system.util.runCommand
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.FileSystem
import okio.Path
import okio.SYSTEM

interface GitWrapper {
    val directory: Path

    fun setCredentials(credentials: Credentials?)

    suspend fun init(initialBranch: String = "main")
    suspend fun clone(url: String)

    suspend fun add(vararg filePatterns: String)
    suspend fun commit(message: String)
    suspend fun checkout(branch: String, createNew: Boolean = false)
    suspend fun checkoutOrphan(branch: String)
    suspend fun fetch(remote: String?)
    suspend fun pull(remote: String?, branch: String?)
    suspend fun push(remote: String?, branch: String? = null)
    suspend fun remoteAdd(name: String, url: String)

    suspend fun getUncommittedFiles(): List<Path>
    suspend fun doesBranchExist(branch: String): Boolean

    data class Credentials(val username: String, val password: String)

    companion object {
        @Throws(GitWrapperCreationException::class)
        fun createSystemDefault(
            directory: Path,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
            fileSystem: FileSystem = FileSystem.SYSTEM
        ): GitWrapper =
            createSystemDefaultGitWrapper(directory, ioDispatcher, fileSystem)

        @Throws(GitWrapperCreationException::class)
        fun createCommandLine(
            directory: Path,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
            fileSystem: FileSystem = FileSystem.SYSTEM
        ): CommandLineGitWrapper =
            CommandLineGitWrapper(getAndCheckGitBinaryPath(), directory, fileSystem, ioDispatcher)
    }
}

@Throws(GitWrapperCreationException::class)
internal fun getAndCheckGitBinaryPath(): String {
    val binaryPath: String = getGitBinaryPath()
    try {
        checkNotNull(runCommand(binaryPath, "--version"))
    }
    catch (e: Throwable) {
        throw GitWrapperCreationException.GitBinaryNotFunctional(binaryPath)
    }
    return binaryPath
}

@Throws(GitWrapperCreationException::class)
internal expect fun createSystemDefaultGitWrapper(
    directory: Path,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    fileSystem: FileSystem = FileSystem.SYSTEM
): GitWrapper
