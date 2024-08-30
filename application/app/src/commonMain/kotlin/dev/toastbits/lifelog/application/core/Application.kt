package dev.toastbits.lifelog.application.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.composable.theme.ApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValuesData
import dev.toastbits.composekit.settings.ui.getDefaultCatppuccinThemes
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourcelist.DatabaseSourceListScreen
import dev.toastbits.lifelog.application.navigation.navigator.Navigator
import dev.toastbits.lifelog.application.navigation.navigator.NavigatorImpl
import dev.toastbits.lifelog.application.settings.data.appsettings.AppSettingsImpl
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings

class Application(
    private val context: PlatformContext,
    private val preferences: PlatformPreferences,
    private val settings: AppSettings = AppSettingsImpl(preferences)
) {
    private val navigator: Navigator = NavigatorImpl(initialScreen = DatabaseSourceListScreen())

    @Composable
    fun Main() {
        val theme: ThemeValuesData = remember {
            getDefaultCatppuccinThemes().first { it.name.lowercase().contains("green") }.theme
        }

        CompositionLocalProvider(LocalSettings provides settings) {
            theme.ApplicationTheme(context) {
                Scaffold { padding ->
                    navigator.CurrentScreen(Modifier.fillMaxSize().padding(padding))
                }
            }
        }
    }

    fun onClose() {

    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return navigator.handleKeyEvent(event)
    }
}
