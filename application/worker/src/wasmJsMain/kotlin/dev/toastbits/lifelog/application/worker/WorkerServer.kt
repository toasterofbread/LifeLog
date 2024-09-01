package dev.toastbits.lifelog.application.worker

import dev.toastbits.lifelog.application.worker.model.WorkerCommand
import dev.toastbits.lifelog.application.worker.model.WorkerCommandResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.DedicatedWorkerGlobalScope
import org.w3c.dom.MessageEvent

private external val self: DedicatedWorkerGlobalScope

internal class WorkerServer {
    private val coroutineScope: CoroutineScope = CoroutineScope(Job())

    private fun msg(message: String): String = "Worker (server): $message"
    private fun log(message: String) = println(msg(message))

    fun handleMessage(message: MessageEvent) {
        val command: WorkerCommand =
            try {
                Json.decodeFromString(message.data.toString())
            }
            catch (e: Throwable) {
                val exception: Throwable = RuntimeException(msg("Deserialising command from '${message.data}' failed"))
                exception.printStackTrace()
                exception.toResult().post()
                return
            }

        coroutineScope.launch {
            val result: WorkerCommandResult =
                command.execute { progress ->
                    WorkerCommandResult.Progress(progress).post()
                }
            result.post()
        }
    }

    fun handleError(message: JsAny? /* Event|String */, filename: String, lineNo: Int, colNo: Int, error: JsAny?) {
        log("Got error $message $filename $lineNo $colNo $error")
    }
}

private fun Throwable.toResult(): WorkerCommandResult.Exception =
    WorkerCommandResult.Exception(this.stackTraceToString(), this::class.toString())

private fun WorkerCommandResult.post() {
    val serialisedResult: String =
        try {
            Json.encodeToString(this)
        }
        catch (e: Throwable) {
            val exception: Throwable = RuntimeException("Worker: Serialising result '$this' failed")
            exception.printStackTrace()
            Json.encodeToString(exception.toResult())
        }

    self.postMessage(serialisedResult.toJsString())
}
