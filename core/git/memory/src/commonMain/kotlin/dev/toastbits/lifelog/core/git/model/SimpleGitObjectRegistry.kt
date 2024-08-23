package dev.toastbits.lifelog.core.git.model

class SimpleGitObjectRegistry: GitObjectRegistry {
    private val objects: MutableMap<String, GitObject> = mutableMapOf()

    override fun writeObject(obj: GitObject) {
        objects[obj.hash] = obj
    }

    override fun readObject(ref: String): GitObject =
        objects[ref] ?: throw NullPointerException("Object with hash '$ref' not found")
}
