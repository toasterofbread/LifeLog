package dev.toastbits.lifelog.application.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.lifelog.application.navigation.navigator.Navigator

interface Screen {
    @Composable
    fun Content(navigator: Navigator, modifier: Modifier)
}
