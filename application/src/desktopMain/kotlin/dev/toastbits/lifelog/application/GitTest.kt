package dev.toastbits.lifelog.application

import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.git.handler.GitCloner
import dev.toastbits.lifelog.core.git.handler.GitCommitGenerator
import dev.toastbits.lifelog.core.git.handler.GitPackFileParser
import dev.toastbits.lifelog.core.git.handler.GitTreeGenerator
import dev.toastbits.lifelog.core.git.handler.GitTreeRenderer
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.provider.ZlibInflater
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import okio.Path.Companion.toPath

suspend fun gitTest() {
    val repositoryUrl: String = "https://github.com/toasterofbread/test"
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
//            println("Cloning ($stage): $r / ${l ?: "?"}")
        }

    val objects: MutableGitObjectRegistry = SimpleGitObjectRegistry()

    val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(16777216))
    val sha1Provider = PlatformSha1Provider()
    val parser: GitPackFileParser = GitPackFileParser(sha1Provider, inflater, objects)
    parser.parsePackFile(content) { stage, r, l ->
//        println("Parsing pack ($stage): $r / $l")
    }

    val headCommit: GitObject = objects.readObject(ref)
    val db: MutableFileStructure = MutableFileStructure()

    val treeRenderer: GitTreeRenderer = GitTreeRenderer(objects)
    treeRenderer.renderCommit(headCommit, db)

    db.createFile("newuitest.kt".toPath(), listOf("Hello", "New", "World!"))
//    db.createFile("uitest.kt".toPath(), listOf("Hello", "World!"), overwrite = true)

    val user: GitCommitGenerator.UserInfo = GitCommitGenerator.UserInfo.ofNow("Talo Halton", "talohalton@gmail.com")
    val message: String =
        """
           Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
           
           Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
           
           Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.             
        """.trimIndent()

    val treeGenerator: GitTreeGenerator = GitTreeGenerator(sha1Provider, objects)
    val commitGenerator: GitCommitGenerator = GitCommitGenerator(treeGenerator, sha1Provider)
    val diff: GitObject = commitGenerator.generateCommitObject(headCommit, db, message, user, user)

    println(diff)
}
