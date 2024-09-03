package dev.toastbits.lifelog.application.worker.command

import dev.toastbits.lifelog.application.worker.mapper.WorkerExecutionContext
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import kotlinx.serialization.Serializable

@Serializable
data class WorkerCommandClearGitRepositoryCache(
    val repositoryUrl: String
): WorkerCommand {
    override suspend fun execute(context: WorkerExecutionContext, onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult {
        TODO()
//        val removedObjects: Int =
//            LocalGitObjectCache.clearRepositoryCache(repositoryUrl) { checked, total ->
//                onProgress(Progress(checked, total))
//            }
//        return WorkerCommandResult.Success(Response(removedObjects))
    }

    @Serializable
    data class Progress(val checkedObjects: Int, val totalObjects: Int): WorkerCommandProgress

    @Serializable
    data class Response(val removedObjects: Int): WorkerCommandResponse
}
