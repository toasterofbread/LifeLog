package dev.toastbits.lifelog.application.worker

import dev.toastbits.lifelog.application.worker.command.WorkerCommand
import dev.toastbits.lifelog.application.worker.command.WorkerCommandProgress
import dev.toastbits.lifelog.application.worker.command.WorkerCommandResponse
import dev.toastbits.lifelog.application.worker.model.TypedWorkerCommandResult
import kotlinx.serialization.json.Json

internal val workerJson: Json = Json { useArrayPolymorphism = true }

expect class WorkerClient {
    suspend inline fun <reified R: WorkerCommandResponse> executeCommand(
        command: WorkerCommand,
        noinline onProgress: (WorkerCommandProgress) -> Unit
    ): Result<TypedWorkerCommandResult<R>>
}
