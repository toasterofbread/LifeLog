package dev.toastbits.lifelog.core.git.model

interface GitObjectRegistry {
    fun writeObject(obj: GitObject)
    fun readObject(ref: String): GitObject
}
