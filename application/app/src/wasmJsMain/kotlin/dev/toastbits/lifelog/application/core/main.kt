package dev.toastbits.lifelog.application.core

import androidx.compose.ui.window.CanvasBasedWindow
import dev.toastbits.composekit.platform.InMemoryPlatformPreferences
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformContextImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun main() {
    val coroutineScope: CoroutineScope = CoroutineScope(Job())
    val context: PlatformContext = PlatformContextImpl(coroutineScope)
    val prefs: InMemoryPlatformPreferences = InMemoryPlatformPreferences()

    val application: Application = Application(context, prefs)

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        application.Main()
    }

    application.onClose()
}
