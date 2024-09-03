package dev.toastbits.lifelog.application.worker.command

import kotlinx.serialization.Serializable

@Serializable
sealed interface WorkerCommandProgress {
    @Serializable
    data class PotentialError(val message: String): WorkerCommandProgress

    @Serializable
    data object WaitingForWorker: WorkerCommandProgress

    @Serializable
    data object WorkerStarted: WorkerCommandProgress
}
