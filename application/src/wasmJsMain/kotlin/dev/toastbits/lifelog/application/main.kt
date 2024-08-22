package dev.toastbits.lifelog.application

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlin.js.Promise

var logText by mutableStateOf("")

fun log(content: Any?) {
    println(content)
    logText += "$content\n"
}

fun main() {
//    GlobalScope.launch {
        test()
//    }

    val application: Application = Application()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
//        application.Main()
        Text(logText)
    }

    application.onClose()
}

//@JsModule("@es-git/save-as-mixin")
//external interface ISaveAsRepo {
//    fun saveText(contents: String): Promise<JsAny>
//}

@JsModule("@es-git/core")
external interface IRawRepo {

}

@JsModule("@es-git/memory-repo")
external class MemoryRepo {
    fun listRefs(): Promise<JsAny>
}

fun test() {
    val r = MemoryRepo()
    log("REPO $r")

    val result = r.listRefs().then(
        onFulfilled = {
            log("FULFILLED ${it::class}")
            it
        },
        onRejected = {
            log("REJECTED $it")
            TODO(it.toString())
        }
    )
    log("DONE $result")
}
