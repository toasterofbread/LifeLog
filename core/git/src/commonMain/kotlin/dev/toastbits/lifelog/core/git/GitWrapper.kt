package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path

interface GitWrapper {
    val directory: Path

    suspend fun init()
    suspend fun clone(url: String)

    suspend fun add(vararg filePatterns: String)
    suspend fun commit(message: String)
    suspend fun pull(remote: String?, branch: String?)
    suspend fun push(remote: String?)
    suspend fun remoteAdd(name: String, url: String)

    companion object {
        @Throws(GitWrapperCreationException::class)
        fun create(directory: Path, dispatcher: CoroutineDispatcher) =
            createGitWrapper(directory, dispatcher)
    }
}

@Throws(GitWrapperCreationException::class)
internal expect fun createGitWrapper(directory: Path, dispatcher: CoroutineDispatcher): GitWrapper
