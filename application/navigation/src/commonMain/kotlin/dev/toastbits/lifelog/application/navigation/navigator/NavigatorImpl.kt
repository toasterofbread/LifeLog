package dev.toastbits.lifelog.application.navigation.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import dev.toastbits.lifelog.application.navigation.Screen
import dev.toastbits.lifelog.application.navigation.composable.CheckModifierUsage
import dev.toastbits.lifelog.application.navigation.content.NavigatorContent

class NavigatorImpl(
    initialScreen: Screen
): Navigator {
    private val stack: MutableList<Screen> = mutableStateListOf(initialScreen)
    private var _currentScreenIndex: Int by mutableStateOf(0)
    private var currentScreenIndex: Int
        get() = _currentScreenIndex
        set(value) {
            _currentScreenIndex = value.coerceIn(0 until stack.size)
        }

    override val currentScreen: Screen
        get() = stack[currentScreenIndex]

    override fun pushScreen(screen: Screen) {
        stack.removeAllButFirst(currentScreenIndex + 1)
        stack.add(screen)
        currentScreenIndex++
    }

    override fun replaceScreen(screen: Screen) {
        stack.removeAllButFirst(currentScreenIndex)
        stack.add(screen)
    }

    override fun canNavigateForward(): Boolean = currentScreenIndex + 1 < stack.size

    override fun canNavigateBackward(): Boolean =  currentScreenIndex > 0

    override fun navigateForward() {
        currentScreenIndex++
    }

    override fun navigateBackward() {
        currentScreenIndex--
    }

    @Composable
    override fun CurrentScreen(modifier: Modifier) {
        CheckModifierUsage(modifier, currentScreen) {
            NavigatorContent(this, it) {
                currentScreen.Content(this, it)
            }
        }
    }

    override fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyUp) {
            return false
        }

        when (keyEvent.key) {
            Key.Escape -> navigateBackward()
            else -> return false
        }
        return true
    }
}

private fun MutableList<*>.removeAllButFirst(n: Int) {
    for (i in 0 until size - n) {
        removeLast()
    }
}
