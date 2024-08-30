package dev.toastbits.lifelog.application.navigation.navigator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import dev.toastbits.lifelog.application.navigation.Screen

interface Navigator {
    @Composable
    fun CurrentScreen(modifier: Modifier)

    val currentScreen: Screen

    fun pushScreen(screen: Screen)
    fun replaceScreen(screen: Screen)

    fun canNavigateForward(): Boolean
    fun canNavigateBackward(): Boolean

    fun navigateForward()
    fun navigateBackward()

    fun handleKeyEvent(keyEvent: KeyEvent): Boolean
}
