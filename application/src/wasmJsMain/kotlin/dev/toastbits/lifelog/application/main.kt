package dev.toastbits.lifelog.application

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.CanvasBasedWindow
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.Promise

var logText by mutableStateOf("")

fun log(content: Any?) {
    println(content)
    logText += "$content\n"
}

fun main() {
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch(Dispatchers.Main) {
        gitTest(Dispatchers.Main)
    }

    val application: Application = Application()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
//        application.Main()
        Text(logText)
    }

    application.onClose()
}
