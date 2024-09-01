package dev.toastbits.lifelog.application.worker.command

import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import kotlinx.serialization.Serializable

@Serializable
sealed interface WorkerCommand {
    suspend fun execute(onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult
}
