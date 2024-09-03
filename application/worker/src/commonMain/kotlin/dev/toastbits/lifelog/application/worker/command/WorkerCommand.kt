package dev.toastbits.lifelog.application.worker.command

import dev.toastbits.lifelog.application.worker.mapper.WorkerExecutionContext
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import kotlinx.serialization.Serializable

@Serializable
sealed interface WorkerCommand {
    suspend fun execute(context: WorkerExecutionContext, onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult
}
