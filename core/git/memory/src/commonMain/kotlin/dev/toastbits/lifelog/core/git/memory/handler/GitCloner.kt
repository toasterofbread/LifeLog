package dev.toastbits.lifelog.core.git.memory.handler

import dev.toastbits.lifelog.core.git.memory.handler.stage.GitHandlerStage
import dev.toastbits.lifelog.core.git.memory.util.GitConstants
import dev.toastbits.lifelog.core.git.core.model.GitCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GitCloner(
    private val ioDispatcher: CoroutineDispatcher,
    private val httpClient: HttpClient
) {
    fun interface ProgressListener {
        fun onProgress(stage: GitHandlerStage.Clone, receivedBytes: Long, contentLength: Long?)
    }

    suspend fun shallowClone(
        repositoryUrl: String,
        branch: String,
        credentials: GitCredentials? = null,
        progressListener: ProgressListener? = null
    ): Pair<ByteArray, String> = withContext(ioDispatcher) {
        val headers: Headers = GitConstants.getDefaultGitRequestHeaders(credentials)

        val headRef: String = getRef(repositoryUrl, branch, headers, progressListener)

        progressListener?.onProgress(GitHandlerStage.Clone.PULL, 0, null)

        val requestBody: ByteArray = (
            "0011command=fetch0016object-format=sha10001000fno-progress"
            + "0032want $headRef\n"
            + "0009done\n0000"
        ).encodeToByteArray()

        val response: HttpResponse =
            httpClient.post("$repositoryUrl/git-upload-pack") {
                setBody(requestBody)
                headers {
                    appendAll(headers)
                }

                onDownload { received, length ->
                    progressListener?.onProgress(GitHandlerStage.Clone.PULL, received, length)
                }
            }

        check(response.status.isSuccess()) { response.status }

        val packFileContent: ByteArray = response.body<ByteArray>()
        validatePackFileContent(packFileContent)

        return@withContext packFileContent to headRef
    }

    private fun requestStr(s: String): String =
        (s.length + 4).toHexString().padStart(4, '0') + s

    private suspend fun getRef(
        repositoryUrl: String,
        branch: String,
        headers: Headers,
        progressListener: ProgressListener?
    ): String {
        progressListener?.onProgress(GitHandlerStage.Clone.RETRIEVE_REF, 0, null)

        val requestBody: ByteArray = (
            "0014command=ls-refs\n" +
            "0014agent=git/2.45.20016object-format=sha100010009peel\n" +
            "000csymrefs\n" +
            "000bunborn\n" +
            requestStr("ref-prefix refs/heads/$branch\n") +
            "0000"
        ).encodeToByteArray()

        val response: HttpResponse = httpClient.post("$repositoryUrl/git-upload-pack") {
            setBody(requestBody)
            headers {
                appendAll(headers)
            }
            onDownload { received, length ->
                progressListener?.onProgress(GitHandlerStage.Clone.RETRIEVE_REF, received, length)
            }
        }
        check(response.status.isSuccess()) { response.status }

        val refLines: List<String> = response.bodyAsText().split("\n")
        for (line in refLines) {
            val split = line.split(' ', limit = 3)
            if (split.size != 2 || split[1] != "refs/heads/$branch") {
                continue
            }

            check(split[0].length - 4 == 40) { line }
            return split[0].drop(4)
        }

        throw NullPointerException("HEAD ref not found in $refLines")
    }

    private fun validatePackFileContent(bytes: ByteArray) {
        check(bytes.isNotEmpty())

        if (bytes.size >= 7 && bytes.decodeToString(4, 7) == "ERR") {
            val size: Int = bytes.decodeToString(0, 4).hexToInt()
            val content: String = bytes.decodeToString(0, size)
            throw RuntimeException(content)
        }
    }
}