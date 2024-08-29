package dev.toastbits.lifelog.application.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformContextImpl
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PlatformPreferencesImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class MainActivity : ComponentActivity() {
    private var application: Application? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coroutineScope: CoroutineScope = CoroutineScope(Job())
        val context: PlatformContext = PlatformContextImpl(this, coroutineScope)
        val prefs: PlatformPreferences = PlatformPreferencesImpl.getInstance(this)

        val currentApplication: Application = Application(context, prefs)
        application = currentApplication

        setContent {
            currentApplication.Main()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        application?.also {
            it.onClose()
            application = null
        }
    }
}
