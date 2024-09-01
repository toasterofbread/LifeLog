package dev.toastbits.lifelog.application.worker.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface WorkerCommand {
    suspend fun execute(onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult

    @Serializable
    data class Echo(val data: String, val times: Int): WorkerCommand {
        override suspend fun execute(onProgress: (WorkerCommandProgress) -> Unit): WorkerCommandResult {
            return WorkerCommandResult.Success(EchoResponse(data.repeat(times)))
        }
    }

    @Serializable
    data class EchoResponse(val data: String): WorkerCommandResponse
}

@Serializable
sealed interface WorkerCommandProgress
