package dev.toastbits.lifelog.application.app

import androidx.compose.ui.window.CanvasBasedWindow
import com.toasterofbread.composekit.platform.BrowserCookies
import com.toasterofbread.composekit.platform.CookiesPlatformPreferences
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformContextImpl
import dev.toastbits.composekit.platform.PlatformPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun main() {
    val coroutineScope: CoroutineScope = CoroutineScope(Job())

    val context: PlatformContext = PlatformContextImpl(coroutineScope)
    val prefs: PlatformPreferences = CookiesPlatformPreferences(BrowserCookies)

    val application: Application = Application(context, prefs)

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        application.Main()
    }

    application.onClose()
}
