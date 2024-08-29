package dev.toastbits.lifelog.application.core

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
import kotlinx.coroutines.CoroutineDispatcher
import okio.Path.Companion.toPath

suspend fun gitTest(ioDispatcher: CoroutineDispatcher) {
    val repositoryUrl: String = "http://localhost:3000/https://github.com/toasterofbread/test.git"
    val branch: String = "main"
    val credentials: GitCredentials? = null

    val client: HttpClient =
        HttpClient() {
            install(HttpTimeout) {
                socketTimeoutMillis = Long.MAX_VALUE
            }
        }

    val cloner: GitCloner = GitCloner(ioDispatcher, client)
    val (content, ref) = cloner.shallowClone(repositoryUrl, branch, credentials) { stage, r, l ->
        println("Cloning ($stage): $r / ${l ?: "?"}")
    }

    val objects: MutableGitObjectRegistry = SimpleGitObjectRegistry()

    val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(16777216))
    val deflater: ZlibDeflater = PlatformZlibDeflater()

    val sha1Provider = PlatformSha1Provider()
    val parser: GitPackFileParser = GitPackFileParser(sha1Provider, inflater, objects)
    parser.parsePackFile(content) { stage, r, l ->
        println("Parsing pack ($stage): $r / $l")
    }

    println("Got ${objects.getAll().size} objects")

    val headCommit: GitObject = objects.readObject(ref)
    val db: MutableFileStructure = MutableFileStructure()

    println("Rendering tree...")

    val treeRenderer: GitTreeRenderer = GitTreeRenderer(objects)
    treeRenderer.renderCommitTree(headCommit, db)

    db.createFile("wasm.kt".toPath(), listOf("Hello", "From", "Kotlin/WASM!"), overwrite = true)

    val user: GitCommitGenerator.UserInfo = GitCommitGenerator.UserInfo.ofNow("Talo Halton", "talohalton@gmail.com")
    val message: String =
        """
           definitely wasm
        """.trimIndent()

    println("Generating new commit...")

    val treeGenerator: GitTreeGenerator = GitTreeGenerator(sha1Provider, objects)
    val commitGenerator: GitCommitGenerator = GitCommitGenerator(treeGenerator, sha1Provider)
    val commit: GitObject = commitGenerator.generateCommitObject(headCommit, db, message, user, user)
    objects.writeObject(commit)

    println("Generating pack file...")

    val packFileGenerator = GitPackFileGenerator(sha1Provider, deflater)
    val packFile: GitPackFileGenerator.PackFile = packFileGenerator.generatePackFile(objects.getAll()) { i, t ->
        println("Writing pack file object ${i + 1} of $t...")
    }

    println("Pushing pack file...")

    val pusher: GitPusher = GitPusher(ioDispatcher, client)
    pusher.pushPackFile(packFile, repositoryUrl, headCommit, commit, credentials)

    println("Done")
}