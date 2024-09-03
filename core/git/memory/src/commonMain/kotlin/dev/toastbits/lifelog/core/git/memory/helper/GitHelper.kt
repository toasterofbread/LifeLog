package dev.toastbits.lifelog.core.git.memory.helper

import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.git.memory.handler.GitCloner
import dev.toastbits.lifelog.core.git.memory.handler.GitPackFileParser
import dev.toastbits.lifelog.core.git.memory.handler.GitTreeRenderer
import dev.toastbits.lifelog.core.git.memory.handler.stage.GitHandlerStage
import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.model.SimpleGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.provider.PlatformSha1Provider
import dev.toastbits.lifelog.core.git.memory.provider.PlatformZlibInflater
import dev.toastbits.lifelog.core.git.memory.provider.ZlibInflater
import dev.toastbits.lifelog.core.git.core.model.GitCredentials
import dev.toastbits.lifelog.core.git.memory.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.model.readObject
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher

class GitHelper(
    private val client: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val workDispatcher: CoroutineDispatcher,
    private val credentials: GitCredentials? = null
) {
    fun interface ProgressListener {
        fun onProgress(stage: GitHandlerStage, part: Long?, total: Long?)
    }

    suspend fun cloneToFileStructure(
        repositoryUrl: String,
        branch: String,
        objectRegistry: MutableGitObjectRegistry?,
        progressListener: ProgressListener? = null
    ): Result<FileStructure> = runCatching {
        val cloner: GitCloner = GitCloner(ioDispatcher, client)
        val (content: ByteArray, ref: String) =
            cloner.shallowClone(repositoryUrl, branch, credentials, objectRegistry) { stage, received, length ->
                progressListener?.onProgress(stage, received, length)
            }

        val objects: MutableGitObjectRegistry = objectRegistry ?: SimpleGitObjectRegistry()
        val inflater: ZlibInflater = PlatformZlibInflater(ByteArray(16777216))

        val sha1Provider = PlatformSha1Provider()
        val parser: GitPackFileParser = GitPackFileParser(sha1Provider, inflater, objects, workDispatcher)
        parser.parsePackFile(content) { stage, itemIndex, totalItems ->
            progressListener?.onProgress(stage, itemIndex?.toLong(), totalItems?.toLong())
        }

        val headCommit: GitObject = objects.readObject(ref)
        val fileStructure: MutableFileStructure = MutableFileStructure()

        val treeRenderer: GitTreeRenderer = GitTreeRenderer(objects)
        treeRenderer.renderCommitTree(headCommit, fileStructure) { readBytes, totalBytes ->
            progressListener?.onProgress(GitHandlerStage.RenderCommitTree, readBytes.toLong(), totalBytes.toLong())
        }

        return@runCatching fileStructure
    }
}
