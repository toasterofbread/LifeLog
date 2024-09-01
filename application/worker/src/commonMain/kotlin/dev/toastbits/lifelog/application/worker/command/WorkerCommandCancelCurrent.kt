package dev.toastbits.lifelog.application.worker.command

import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import dev.toastbits.lifelog.application.worker.model.toResult
import kotlinx.serialization.Serializable

@Serializable
internal data object WorkerCommandCancelCurrent: WorkerCommand {
    override suspend fun execute(onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult =
        IllegalStateException("WorkerCommandCancelCurrent should not be executed").toResult()
}
