package dev.toastbits.lifelog.application.worker

import dev.toastbits.lifelog.application.worker.model.TypedWorkerCommandResult
import dev.toastbits.lifelog.application.worker.command.WorkerCommand
import dev.toastbits.lifelog.application.worker.command.WorkerCommandProgress
import dev.toastbits.lifelog.application.worker.command.WorkerCommandResponse
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

internal val workerJson: Json = Json { useArrayPolymorphism = true }

expect object WorkerClient {
    suspend inline fun <reified R: WorkerCommandResponse> executeCommand(
        command: WorkerCommand,
        progressClass: KClass<out WorkerCommandProgress>?,
        crossinline onProgress: (WorkerCommandProgress) -> Unit
    ): Result<TypedWorkerCommandResult<R>>
}

suspend inline fun <reified R: WorkerCommandResponse> WorkerClient.executeCommand(
    command: WorkerCommand
): Result<TypedWorkerCommandResult<R>> =
    executeCommand(command, null) { throw IllegalStateException(it.toString()) }

suspend inline fun <reified R: WorkerCommandResponse, reified P: WorkerCommandProgress> WorkerClient.executeCommand(
    command: WorkerCommand,
    crossinline onProgress: (P) -> Unit
): Result<TypedWorkerCommandResult<R>> =
    executeCommand(command, P::class) { onProgress(it as P) }
