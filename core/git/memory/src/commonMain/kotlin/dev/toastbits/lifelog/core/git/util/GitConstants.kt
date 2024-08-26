package dev.toastbits.lifelog.core.git.util

import dev.toastbits.lifelog.core.git.model.GitCredentials
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObject.Type.BLOB
import dev.toastbits.lifelog.core.git.model.GitObject.Type.COMMIT
import dev.toastbits.lifelog.core.git.model.GitObject.Type.NONE
import dev.toastbits.lifelog.core.git.model.GitObject.Type.OFS_DELTA
import dev.toastbits.lifelog.core.git.model.GitObject.Type.REF_DELTA
import dev.toastbits.lifelog.core.git.model.GitObject.Type.TAG
import dev.toastbits.lifelog.core.git.model.GitObject.Type.TREE
import dev.toastbits.lifelog.core.git.model.GitObject.Type.UNUSED
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal object GitConstants {
    const val GIT_VERSION: Int = 2
    const val OBJECT_HEADER_TYPE_BIT_COUNT: Int = 3

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

    fun getDefaultGitRequestHeaders(credentials: GitCredentials?): Headers =
        HeadersBuilder().apply {
            append("Git-Protocol", "version=2")
            if (credentials != null) {
                append("Authorization", credentials.toAuthorizationHeader())
            }
        }.build()

    @OptIn(ExperimentalEncodingApi::class)
    private fun GitCredentials.toAuthorizationHeader(): String =
        "Basic " + Base64.encode("$username:$password".encodeToByteArray())
}
