package dev.toastbits.lifelog.core.git.util

import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObject.Type.BLOB
import dev.toastbits.lifelog.core.git.model.GitObject.Type.COMMIT
import dev.toastbits.lifelog.core.git.model.GitObject.Type.NONE
import dev.toastbits.lifelog.core.git.model.GitObject.Type.OFS_DELTA
import dev.toastbits.lifelog.core.git.model.GitObject.Type.REF_DELTA
import dev.toastbits.lifelog.core.git.model.GitObject.Type.TAG
import dev.toastbits.lifelog.core.git.model.GitObject.Type.TREE
import dev.toastbits.lifelog.core.git.model.GitObject.Type.UNUSED

internal object GitConstants {
    object TreeMode {
        const val TREE: Int = 40000
        const val NORMAL_FILE: Int = 100644
        const val EXECUTABLE_FILE: Int = 100755
    }

    fun getObjectTypeIdentifier(objectType: GitObject.Type): String =
        when (objectType) {
            COMMIT -> "commit"
            TREE -> "tree"
            BLOB -> "blob"
            TAG -> "tag"
            OFS_DELTA -> "ofs_delta"
            REF_DELTA -> "ref_delta"

            NONE,
            UNUSED -> throw IllegalStateException(objectType.name)
        }
}
