package dev.toastbits.lifelog.application.navigation.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced

@Composable
fun <T> CheckModifierUsage(
    modifier: Modifier,
    key: Any?,
    onModifierNotUsed: (key: Any?) -> Unit = { throw RuntimeException("Passed modifier was not used by key $it") },
    content: @Composable (Modifier) -> T
) {
    var modifierUsed: Boolean by remember { mutableStateOf(false) }

    content(modifier.onPlaced { modifierUsed = true })

    DisposableEffect(key) {
        val currentKey: Any? = key
        onDispose {
            if (!modifierUsed) {
                onModifierNotUsed(currentKey)
            }
            modifierUsed = false
        }
    }
}
