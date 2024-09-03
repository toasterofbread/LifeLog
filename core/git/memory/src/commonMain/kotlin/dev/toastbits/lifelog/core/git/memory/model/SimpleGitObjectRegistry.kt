package dev.toastbits.lifelog.core.git.memory.model

class SimpleGitObjectRegistry: MutableGitObjectRegistry {
    private val objects: MutableMap<String, GitObject> = mutableMapOf()

    override suspend fun getAvailableObjects(type: GitObject.Type?): Iterable<GitObjectRegistry.GitObjectInfo> =
        objects
            .asSequence()
            .mapNotNull { obj ->
                if (type != null && type != obj.value.type) {
                    return@mapNotNull null
                }
                return@mapNotNull GitObjectRegistry.GitObjectInfo(obj.key, obj.value.type)
            }.asIterable()

    override suspend fun readObjectOrNull(ref: String): GitObject? =
        objects[ref]

    override suspend fun writeObject(obj: GitObject) {
        objects[obj.hash] = obj
    }
}
