package dev.toastbits.lifelog.core.git.memory.model

interface GitObjectRegistry {
    suspend fun getAvailableObjects(type: GitObject.Type?): Iterable<GitObjectInfo>
    suspend fun readObjectOrNull(ref: String): GitObject?

    data class GitObjectInfo(val hash: String, val type: GitObject.Type)
}

suspend fun GitObjectRegistry.readObject(ref: String): GitObject =
    readObjectOrNull(ref) ?: throw NullPointerException("Object with hash '$ref' not found")

interface MutableGitObjectRegistry: GitObjectRegistry {
    suspend fun writeObject(obj: GitObject)
}
