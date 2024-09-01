package dev.toastbits.lifelog.application.worker

import dev.toastbits.lifelog.application.worker.model.TypedWorkerCommandResult
import dev.toastbits.lifelog.application.worker.model.WorkerCommand
import dev.toastbits.lifelog.application.worker.model.WorkerCommandProgress
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResponse
import dev.toastbits.lifelog.application.worker.model.cast
import kotlinx.coroutines.sync.Mutex
import kotlin.reflect.KClass

actual object WorkerClient {
    private val mutex: Mutex = Mutex()

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    actual suspend inline fun <reified R : WorkerCommandResponse> executeCommand(
        command: WorkerCommand,
        progressClass: KClass<out WorkerCommandProgress>?,
        crossinline onProgress: (WorkerCommandProgress) -> Unit
    ): Result<TypedWorkerCommandResult<R>> {
        if (!mutex.tryLock()) {
            return Result.failure(ConcurrentModificationException("WorkerClient does not support simultaneous commands ($command)"))
        }

        val result: TypedWorkerCommandResult<R> =
            command.execute { progress ->
                if (progressClass?.isInstance(progress) == true) {
                    onProgress(progress)
                }
            }.cast()

        mutex.unlock()

        return Result.success(result)
    }
}
