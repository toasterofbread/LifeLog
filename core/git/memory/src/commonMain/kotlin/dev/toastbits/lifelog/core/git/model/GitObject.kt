package dev.toastbits.lifelog.core.git.model

import dev.toastbits.lifelog.core.git.util.indexOfOrNull

@Suppress("ArrayInDataClass")
data class GitObject(val bytes: ByteArray, val type: Type, val hash: String) {
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
}
