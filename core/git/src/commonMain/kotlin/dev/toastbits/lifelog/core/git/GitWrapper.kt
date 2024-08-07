package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import java.io.File

expect class GitWrapper(
    directory: File,
    dispatcher: CoroutineDispatcher
)  {
    suspend fun init()
    suspend fun clone(url: String)

    suspend fun add(vararg filePatterns: String)
    suspend fun commit(message: String)
    suspend fun pull(remote: String?)
    suspend fun push(remote: String?)
    suspend fun remoteAdd(name: String, url: String)
}

