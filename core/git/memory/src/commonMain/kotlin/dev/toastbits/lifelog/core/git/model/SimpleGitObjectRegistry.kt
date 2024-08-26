package dev.toastbits.lifelog.core.git.model

class SimpleGitObjectRegistry: MutableGitObjectRegistry {
    private val objects: MutableMap<String, GitObject> = mutableMapOf()
    override fun getObjectsHashCode(): Int = objects.hashCode()

    override fun getAll(): Collection<GitObject> = objects.values

    override fun writeObject(obj: GitObject) {
        objects[obj.hash] = obj
    }

    override fun readObject(ref: String): GitObject =
        objects[ref] ?: throw NullPointerException("Object with hash '$ref' not found")

    override fun readObjectOrNull(ref: String): GitObject? =
        objects[ref]
}
