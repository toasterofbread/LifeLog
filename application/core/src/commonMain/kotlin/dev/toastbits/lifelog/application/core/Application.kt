package dev.toastbits.lifelog.application.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.composable.ApplicationTheme
import dev.toastbits.composekit.platform.composable.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPageWithItems
import dev.toastbits.composekit.settings.ui.ThemeValuesData
import dev.toastbits.composekit.settings.ui.getDefaultCatppuccinThemes

class Application(
    private val context: PlatformContext,
    private val preferences: PlatformPreferences
) {
    @Composable
    fun Main() {
        val theme: ThemeValuesData = remember {
            getDefaultCatppuccinThemes().first { it.name.lowercase().contains("green") }.theme
        }

        val settingsInterface: SettingsInterface = remember {
            SettingsInterface(
                context,
                preferences,
                { theme },
                0,
                { _, _ ->
                    SettingsPageWithItems({ null }, { emptyList() })
                }
            )
        }

        theme.ApplicationTheme(context) {
            Column(Modifier.fillMaxSize()) {
                Text("Hello World! $context")
//                i.Interface(Modifier.fillMaxSize().weight(1f))

                Box(Modifier.size(50.dp).background(LocalApplicationTheme.current.accent))
            }
        }
    }

    fun onClose() {

    }
}
