package dev.toastbits.lifelog.application

import dev.toastbits.lifelog.core.git.handler.GitPackFileParser
import dev.toastbits.lifelog.core.git.handler.GitTreeRenderer
import dev.toastbits.lifelog.core.git.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

suspend fun getContentRemote(): Pair<ByteArray, Map<String, String>> = withContext(Dispatchers.IO) {
//    val data: ByteArray = Base64.decode("""MDAxMWNvbW1hbmQ9ZmV0Y2gwMDE0YWdlbnQ9Z2l0LzIuNDUuMjAwMTZvYmplY3QtZm9ybWF0PXNoYTEwMDAxMDAwZHRoaW4tcGFjazAwMGZpbmNsdWRlLXRhZzAwMGRvZnMtZGVsdGEwMDBjZGVlcGVuIDEwMDMyd2FudCAwNmE0NDViMmU5YjYyOTI3ZGRlMDliN2UwNjU1OTUyZWI0ZWI2ZThkCjAwMzJ3YW50IDA2YTQ0NWIyZTliNjI5MjdkZGUwOWI3ZTA2NTU5NTJlYjRlYjZlOGQKMDAwOWRvbmUKMDAwMA==""")
//    val data: ByteArray = Base64.decode("""MDAxMWNvbW1hbmQ9ZmV0Y2gwMDE0YWdlbnQ9Z2l0LzIuNDUuMjAwMTZvYmplY3QtZm9ybWF0PXNoYTEwMDAxMDAwZHRoaW4tcGFjazAwMGZpbmNsdWRlLXRhZzAwMGRvZnMtZGVsdGEwMDBjZGVlcGVuIDEwMDMyd2FudCAwNmE0NDViMmU5YjYyOTI3ZGRlMDliN2UwNjU1OTUyZWI0ZWI2ZThkCjAwMDlkb25lCjAwMDA=""")

    val client: HttpClient = HttpClient()

    val refLines = client.get("https://github.com/toasterofbread/kjna/info/refs?service=git-upload-pack").bodyAsText().split("\n")
    val refs = refLines
        .mapNotNull { line ->
            val bs1 = line.drop(4).takeIf { it.isNotEmpty() && !it.startsWith("#") }
            bs1?.split("\u0000")?.firstOrNull()?.let { bs2 ->
                val bs = if (bs2.endsWith("HEAD")) bs2.drop(4) else bs2
                bs.split(" ").takeIf { it.size == 2 }?.let { it[1] to it[0] }
            }
        }
        .toMap()

    val data = (
        "0011command=fetch0001000fno-progress"
        + refs.values.joinToString("") { "0032want $it\n" }
        + "0009done\n0000"
    ).encodeToByteArray()

    val headers: Map<String, String> =
        mapOf(
            "Git-Protocol" to "version=2"
        )

    val response: HttpResponse =
        client.post("https://github.com/toasterofbread/kjna/git-upload-pack") {
            setBody(data)
            for ((key, value) in headers) {
                header(key, value)
            }
        }

    check(response.status.isSuccess()) { response.status }

    val content: ByteArray = response.body<ByteArray>()

    return@withContext content to refs
}

suspend fun gitTest() {
    val (content: ByteArray, refs: Map<String, String>) = getContentRemote()

    val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(1048576))
    val cloner: GitPackFileParser = GitPackFileParser(PlatformSha1Provider(), inflater, SimpleGitObjectRegistry())
    cloner.parsePackFile(content)

    val commit = cloner.readObject(refs["HEAD"]!!)
    val path: Path = "/home/toaster/Downloads/test/kt".toPath()
    val treeRenderer: GitTreeRenderer = GitTreeRenderer(cloner, FileSystem.SYSTEM)
    treeRenderer.renderCommit(commit, path)
}
