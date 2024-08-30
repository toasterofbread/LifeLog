package dev.toastbits.lifelog.application.navigation.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.lifelog.application.navigation.navigator.Navigator

@Composable
internal expect fun NavigatorContent(navigator: Navigator, modifier: Modifier, content: @Composable (Modifier) -> Unit)
