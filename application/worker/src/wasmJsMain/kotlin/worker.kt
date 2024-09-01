import dev.toastbits.lifelog.application.worker.WorkerServer
import org.w3c.dom.DedicatedWorkerGlobalScope

private external val self: DedicatedWorkerGlobalScope

fun main() {
    val server: WorkerServer = WorkerServer()
    println("Worker started")

    // First message required for activation of worker?
    // See WorkerClient.firstMessageSkipped
    self.postMessage("Worker started".toJsString())

    self.onmessage = server::handleMessage
    self.onerror = { a, b, c, d, e -> server.handleError(a, b, c, d, e); null }
}
