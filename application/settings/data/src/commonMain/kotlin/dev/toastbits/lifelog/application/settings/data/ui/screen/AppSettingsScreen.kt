package dev.toastbits.lifelog.application.settings.data.ui.screen

import dev.toastbits.composekit.settings.ui.screen.PreferencesTopScreen
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import lifelog.application.settings.data.generated.resources.Res
import lifelog.application.settings.data.generated.resources.screen_app_settings
import org.jetbrains.compose.resources.stringResource

fun AppSettingsScreen(settings: AppSettings): PreferencesTopScreen =
    PreferencesTopScreen(
        settings.allGroups,
        { stringResource(Res.string.screen_app_settings) }
    )
