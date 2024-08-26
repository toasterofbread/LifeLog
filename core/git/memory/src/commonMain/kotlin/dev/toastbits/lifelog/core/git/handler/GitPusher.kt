package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.git.model.GitCredentials
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.util.GitConstants
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GitPusher(
    private val ioDispatcher: CoroutineDispatcher,
    private val httpClient: HttpClient
) {
    suspend fun pushPackFile(
        packFile: GitPackFileGenerator.PackFile,
        repositoryUrl: String,
        headCommit: GitObject,
        latestCommit: GitObject,
        credentials: GitCredentials? = null
    ) = withContext(ioDispatcher) {
        require(headCommit.type == GitObject.Type.COMMIT)
        require(latestCommit.type == GitObject.Type.COMMIT)

        val headers: Headers = GitConstants.getDefaultGitRequestHeaders(credentials)

        val requestBodyHeader: ByteArray = (
            "0035shallow ${headCommit.hash}\n" +
            "00a9${headCommit.hash} ${latestCommit.hash} refs/heads/main\u0000 report-status-v2 side-band-64k object-format=sha1 agent=git/2.45.20000"
        ).encodeToByteArray()

        val requestBody = ByteArray(requestBodyHeader.size + packFile.size)
        requestBodyHeader.copyInto(requestBody)
        packFile.bytes.copyInto(requestBody, requestBodyHeader.size, endIndex = packFile.size)

        val response: HttpResponse =
            httpClient.post("$repositoryUrl/git-receive-pack") {
                setBody(requestBody)
                headers {
                    appendAll(headers)
                }
            }

        check(response.status.isSuccess()) { response.status }

        val parts: List<String> =
            response.bodyAsText()
                .split('\n').asSequence()
                .flatMap { it.split('\u0000') }
                .flatMap { it.split('\u0001') }
                .flatMap { it.split('\u0002') }
                .map { it.drop(4) }
                .filter { it.isNotBlank() && it != "0000" }
                .toList()

        val disallowedParts: List<String> = parts.filter { !isResponsePartAllowed(it) }
        if (disallowedParts.isNotEmpty()) {
            throw RuntimeException("Got unknown response part(s), an error may have occurred: $disallowedParts")
        }
    }

    private fun isResponsePartAllowed(part: String): Boolean {
        if (part == "unpack ok" || part.startsWith("ok ")) {
            return true
        }
        return false
    }
}
