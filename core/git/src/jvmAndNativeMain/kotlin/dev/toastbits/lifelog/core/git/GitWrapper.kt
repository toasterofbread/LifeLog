package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

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
        fun create(directory: Path, dispatcher: CoroutineDispatcher) =
            createGitWrapper(directory, dispatcher)
    }
}

fun GitWrapper.resolve(path: Path): Path =
    directory.resolve(path)

@Throws(GitWrapperCreationException::class)
internal expect fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper
