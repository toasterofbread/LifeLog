package dev.toastbits.lifelog.application.worker

import dev.toastbits.lifelog.application.worker.model.TypedWorkerCommandResult
import dev.toastbits.lifelog.application.worker.model.WorkerCommand
import dev.toastbits.lifelog.application.worker.model.WorkerCommandProgress
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResponse
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import dev.toastbits.lifelog.application.worker.model.cast
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.ErrorEvent
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import kotlin.reflect.KClass

actual object WorkerClient {
    private val worker: Worker = Worker("worker.js")
    private val mutex: Mutex = Mutex()
    private val resultChannel: Channel<WorkerCommandResult> = Channel()

    private var firstMessageSkipped: Boolean = false

    init {
        worker.onerror = { handleError(it as ErrorEvent) }
        worker.onmessage = ::handleMessage
    }

    private fun msg(message: String): String = "Worker (client): $message"
    private fun log(message: String) = println(msg(message))

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    actual suspend inline fun <reified R: WorkerCommandResponse> executeCommand(
        command: WorkerCommand,
        progressClass: KClass<out WorkerCommandProgress>?,
        crossinline onProgress: (WorkerCommandProgress) -> Unit
    ): Result<TypedWorkerCommandResult<R>> {
        while (!firstMessageSkipped) {
            delay(10)
        }

        if (!mutex.tryLock()) {
            return Result.failure(ConcurrentModificationException("WorkerClient does not support simultaneous commands ($command)"))
        }

        try {
            val serialisedCommand: String =
                try {
                    Json.encodeToString(command)
                }
                catch (e: Throwable) {
                    return Result.failure(RuntimeException(msg("Serialising command '$command' failed"), e))
                }

            worker.postMessage(serialisedCommand.toJsString())

            while (true) {
                val result: WorkerCommandResult = resultChannel.receive()
                when (result) {
                    is WorkerCommandResult.Success ->
                        when (val response: WorkerCommandResponse = result.response) {
                            is R -> return Result.success(result.cast())
                            else -> return Result.failure(RuntimeException(msg("Received response of unknown type '${response::class}' while waiting for '${R::class}' ($response)")))
                        }
                    is WorkerCommandResult.Progress -> {
                        val progress: WorkerCommandProgress = result.progress
                        if (progressClass?.isInstance(progress) == true) {
                            onProgress(progress)
                        }
                        else {
                            log("Received progress of unknown type '${progress::class}' while waiting for '$progressClass', ignoring ($progress)")
                        }
                    }
                    is WorkerCommandResult.Exception -> return Result.success(result.cast())
                }
            }
        }
        finally {
            mutex.unlock()
        }
    }

    private fun handleMessage(message: MessageEvent) {
        if (!firstMessageSkipped) {
            log("Skipping first message (${message.data})")
            firstMessageSkipped = true
            return
        }

        val response: WorkerCommandResult =
            try {
                Json.decodeFromString(message.data.toString())
            }
            catch (e: Throwable) {
                RuntimeException(msg("Message '${message.data}' could not be deserialised"), e).printStackTrace()
                return
            }

        val result: ChannelResult<Unit> = resultChannel.trySend(response)
        result.exceptionOrNull()?.also { exception ->
            RuntimeException(msg("Message '$message' was not consumed"), exception).printStackTrace()
        }
    }

    private fun handleError(error: ErrorEvent) {
        if (error.message == "ReferenceError: module is not defined") {
            return
        }

        log("Got error ${error.message} ${error.filename} ${error.lineno} ${error.colno} ${error.error}")
    }
}
