package dev.toastbits.lifelog.core.git.model

import dev.toastbits.lifelog.core.git.provider.Sha1Provider
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

        init {
//            check(ordinal != 0 && ordinal != 5) { ordinal }
        }

        val identifier: String
            get() = when (this) {
                COMMIT -> "commit"
                TREE -> "tree"
                BLOB -> "blob"
                TAG -> "tag"
                OFS_DELTA -> "ofs_delta"
                REF_DELTA -> "ref_delta"

                NONE,
                UNUSED -> throw IllegalStateException(name)
            }
    }

    companion object {
        fun create(type: Type, content: ByteArray, contentSize: Int, sha1Provider: Sha1Provider): GitObject {
            val prefixedBytes: ByteArray = "${type.identifier} $contentSize".encodeToByteArray() + 0b0 + content.take(contentSize)

            val hash: String = sha1Provider.calculateSha1Hash(prefixedBytes)
            return GitObject(prefixedBytes, type, hash)
        }
    }
}
