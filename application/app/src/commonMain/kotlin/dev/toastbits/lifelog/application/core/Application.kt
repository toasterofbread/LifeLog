package dev.toastbits.lifelog.application.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.composable.theme.ApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValuesData
import dev.toastbits.composekit.settings.ui.getDefaultCatppuccinThemes
import dev.toastbits.lifelog.application.core.ui.PersistentTopBar
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourcelist.DatabaseSourceListScreen
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.navigation.navigator.ExtendableNavigator
import dev.toastbits.composekit.platform.LocalContext
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.composekit.utils.common.plus
import dev.toastbits.lifelog.application.settings.data.appsettings.AppSettingsImpl
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.data.ui.screen.AppSettingsScreen
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings

class Application(
    private val context: PlatformContext,
    preferences: PlatformPreferences,
    private val settings: AppSettings = AppSettingsImpl(preferences)
) {
    private val navigator: Navigator = ExtendableNavigator(initialScreen = DatabaseSourceListScreen())
//    private val navigator: Navigator = ExtendableNavigator(initialScreen = AppSettingsScreen(settings))

    @Composable
    fun Main() {
        val theme: ThemeValuesData = remember {
            getDefaultCatppuccinThemes().first { it.name.lowercase().contains("green") }.theme
        }

        CompositionLocalProvider(
            LocalSettings provides settings,
            LocalContext provides context,
            LocalNavigator provides navigator,
        ) {
            theme.ApplicationTheme(context) {
                TopContent()
            }
        }
    }

    @Composable
    private fun TopContent() {
        Scaffold { padding ->
            navigator.CurrentScreen(
                Modifier.fillMaxSize(),
                padding + PaddingValues(30.dp)
            ) { modifier, paddingValues, content ->
                Column(
                    modifier,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PersistentTopBar(Modifier.fillMaxWidth().padding(paddingValues.copy(bottom = 0.dp)))
                    content(Modifier.fillMaxSize().weight(1f), paddingValues.copy(top = 0.dp))
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
