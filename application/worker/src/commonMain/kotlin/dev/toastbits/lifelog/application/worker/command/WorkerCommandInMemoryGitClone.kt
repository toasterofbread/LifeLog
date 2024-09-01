package dev.toastbits.lifelog.application.worker.command

import dev.toastbits.lifelog.application.worker.mapper.serialise
import dev.toastbits.lifelog.application.worker.model.SerialisableFileStructure
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import dev.toastbits.lifelog.application.worker.model.toResult
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.countFiles
import dev.toastbits.lifelog.core.git.core.model.GitCredentials
import dev.toastbits.lifelog.core.git.memory.handler.stage.GitHandlerStage
import dev.toastbits.lifelog.core.git.memory.helper.GitHelper
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable

@Serializable
data class WorkerCommandInMemoryGitClone(
    val repositoryUrl: String,
    val branchName: String,
    val gitCredentials: GitCredentials?
): WorkerCommand {
    override suspend fun execute(onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult {
        val httpClient: HttpClient = HttpClient()
        val gitHelper: GitHelper = GitHelper(httpClient, Dispatchers.Default, Dispatchers.Default, gitCredentials)

        val fileStructure: FileStructure =
            gitHelper.cloneToFileStructure(repositoryUrl, branchName) { stage, part, total ->
                onProgress(Progress(stage, part, total))
            }.fold(
                onSuccess = { it },
                onFailure = { return RuntimeException("Cloning $repositoryUrl:$branchName with $gitCredentials failed", it).toResult() }
            )

        val totalFiles: Long = fileStructure.countFiles().toLong()
        val serialisedFileStructure: SerialisableFileStructure =
            try {
                fileStructure.serialise { done ->
                    onProgress(Progress(GitHandlerStage.SerialisingFileStructure, done.toLong(), totalFiles))
                }
            }
            catch (e: Throwable) {
                return RuntimeException("Serialising file structure from $repositoryUrl:$branchName with $gitCredentials failed", e).toResult()
            }

        return WorkerCommandResult.Success(Response(serialisedFileStructure))
    }

    @Serializable
    data class Progress(val stage: GitHandlerStage, val part: Long?, val total: Long?): WorkerCommandProgress

    @Serializable
    data class Response(val fileStructure: SerialisableFileStructure): WorkerCommandResponse
}
