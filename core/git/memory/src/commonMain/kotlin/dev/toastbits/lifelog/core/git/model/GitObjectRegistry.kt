package dev.toastbits.lifelog.core.git.model

interface GitObjectRegistry {
    fun getAll(): Collection<GitObject>
    fun readObject(ref: String): GitObject
    fun readObjectOrNull(ref: String): GitObject?
}

interface MutableGitObjectRegistry: GitObjectRegistry {
    fun writeObject(obj: GitObject)
}
