package dev.toastbits.lifelog.application

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.core.specification.testFunc

@Composable
fun App() {
    Text("Hello World! " + testFunc())
}
