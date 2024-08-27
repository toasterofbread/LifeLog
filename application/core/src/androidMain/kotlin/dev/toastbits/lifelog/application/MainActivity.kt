package dev.toastbits.lifelog.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.toastbits.lifelog.application.core.Application

class MainActivity : ComponentActivity() {
    private var application: Application? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentApplication: Application = Application()
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
