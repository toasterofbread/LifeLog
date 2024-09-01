package dev.toastbits.lifelog.application.worker.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface WorkerCommandResult {
    @Serializable
    data class Success(val response: WorkerCommandResponse): WorkerCommandResult
    @Serializable
    data class Progress(val progress: WorkerCommandProgress): WorkerCommandResult
    @Serializable
    data class Exception(val stackTraceString: String, val exceptionClass: String): WorkerCommandResult
}
