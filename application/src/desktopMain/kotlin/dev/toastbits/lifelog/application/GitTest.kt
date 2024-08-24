package dev.toastbits.lifelog.application

import dev.toastbits.lifelog.core.git.handler.GitCloner
import dev.toastbits.lifelog.core.git.handler.GitPackFileParser
import dev.toastbits.lifelog.core.git.handler.GitTreeRenderer
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

suspend fun gitTest() {
    val repositoryUrl: String = "https://github.com/toasterofbread/kjna"
    val branch: String = "main"
    val credentials: GitCloner.Credentials? = null

    val client: HttpClient =
        HttpClient() {
            install(HttpTimeout) {
                socketTimeoutMillis = Long.MAX_VALUE
            }
        }

    val cloner: GitCloner = GitCloner(Dispatchers.IO, client)
    val (content: ByteArray, ref: String) =
        cloner.shallowClone(repositoryUrl, branch, credentials) { stage, r, l ->
            println("Cloning ($stage): $r / ${l ?: "?"}")
        }

    val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(16777216))
    val parser: GitPackFileParser = GitPackFileParser(PlatformSha1Provider(), inflater, SimpleGitObjectRegistry())
    parser.parsePackFile(content) { stage, r, l ->
        println("Parsing pack ($stage): $r / $l")
    }

    val commit: GitObject = parser.readObject(ref)
    val path: Path = "/home/toaster/Downloads/test/kt".toPath()
    val treeRenderer: GitTreeRenderer = GitTreeRenderer(parser, FileSystem.SYSTEM)
    treeRenderer.renderCommit(commit, path)
}
