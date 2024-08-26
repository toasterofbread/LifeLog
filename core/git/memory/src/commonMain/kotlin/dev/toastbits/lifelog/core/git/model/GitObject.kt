package dev.toastbits.lifelog.core.git.model

import dev.toastbits.lifelog.core.git.util.indexOfOrNull

data class GitObject(val bytes: ByteArray, val type: Type, val hash: String) {
    init {
        check(bytes.contains(0b0))
    }

    fun findContentStart(): Int =
        bytes.indexOfOrNull(0b0)!! + 1

    enum class Type {
        NONE,
        COMMIT,
        TREE,
        BLOB,
        TAG,
        UNUSED,
        OFS_DELTA,
        REF_DELTA;
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GitObject

        if (!bytes.contentEquals(other.bytes)) return false
        if (type != other.type) return false
        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + hash.hashCode()
        return result
    }
}
