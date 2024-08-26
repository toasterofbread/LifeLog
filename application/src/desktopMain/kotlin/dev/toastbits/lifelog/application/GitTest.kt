package dev.toastbits.lifelog.application

import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.git.handler.GitCloner
import dev.toastbits.lifelog.core.git.handler.GitCommitGenerator
import dev.toastbits.lifelog.core.git.handler.GitPackFileGenerator
import dev.toastbits.lifelog.core.git.handler.GitPackFileParser
import dev.toastbits.lifelog.core.git.handler.GitPusher
import dev.toastbits.lifelog.core.git.handler.GitTreeGenerator
import dev.toastbits.lifelog.core.git.handler.GitTreeRenderer
import dev.toastbits.lifelog.core.git.model.GitCredentials
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.provider.PlatformZlibDeflater
import dev.toastbits.lifelog.core.git.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.provider.ZlibDeflater
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import kotlin.io.encoding.ExperimentalEncodingApi

suspend fun gitTest() {
    val repositoryUrl: String = "https://github.com/toasterofbread/test"
    val branch: String = "main"
    val credentials: GitCredentials? = null

    val client: HttpClient =
        HttpClient() {
            install(HttpTimeout) {
                socketTimeoutMillis = Long.MAX_VALUE
            }
        }

    val cloner: GitCloner = GitCloner(Dispatchers.IO, client)
    val (content: ByteArray, ref: String) =
        cloner.shallowClone(repositoryUrl, branch, credentials) { stage, r, l ->
//            println("Cloning ($stage): $r / ${l ?: "?"}")
        }

    val objects: MutableGitObjectRegistry = SimpleGitObjectRegistry()

    val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(16777216))
    val deflater: ZlibDeflater = PlatformZlibDeflater()

    val sha1Provider = PlatformSha1Provider()
    val parser: GitPackFileParser = GitPackFileParser(sha1Provider, inflater, objects)
    parser.parsePackFile(content) { stage, r, l ->
//        println("Parsing pack ($stage): $r / $l")
    }

    val headCommit: GitObject = objects.readObject(ref)
    val db: MutableFileStructure = MutableFileStructure()

    val treeRenderer: GitTreeRenderer = GitTreeRenderer(objects)
    treeRenderer.renderCommit(headCommit, db)

    db.createFile("uitest.kt".toPath(), listOf("Hello", "World!", "4"), overwrite = true)

    val user: GitCommitGenerator.UserInfo = GitCommitGenerator.UserInfo.ofNow("Talo Halton", "talohalton@gmail.com")
    val message: String =
        """
           E
        """.trimIndent()

    val treeGenerator: GitTreeGenerator = GitTreeGenerator(sha1Provider, objects)
    val commitGenerator: GitCommitGenerator = GitCommitGenerator(treeGenerator, sha1Provider)
    val commit: GitObject = commitGenerator.generateCommitObject(headCommit, db, message, user, user)
    objects.writeObject(commit)

    val packFileGenerator = GitPackFileGenerator(sha1Provider, deflater)
    val packFile: GitPackFileGenerator.PackFile = packFileGenerator.generatePackFile(objects.getAll())

    val pusher: GitPusher = GitPusher(Dispatchers.IO, client)
    pusher.pushPackFile(packFile, repositoryUrl, headCommit, commit, credentials)
}
