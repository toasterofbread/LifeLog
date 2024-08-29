package dev.toastbits.lifelog.application.core

import androidx.compose.ui.window.singleWindowApplication
import dev.toastbits.composekit.platform.PlatformContextImpl
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PlatformPreferencesJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import lifelog.application.app.generated.resources.Res
import lifelog.application.app.generated.resources.app_name
import org.jetbrains.compose.resources.getString

fun main() = runBlocking {
    val coroutineScope: CoroutineScope = CoroutineScope(Job())
    val context: PlatformContextImpl = PlatformContextImpl(getString(Res.string.app_name), coroutineScope)
    val prefs: PlatformPreferences = PlatformPreferencesJson(context.getFilesDir()!!.resolve("settings.json"))

    val application: Application = Application(context, prefs)
    singleWindowApplication {
        application.Main()
    }

    application.onClose()
}
