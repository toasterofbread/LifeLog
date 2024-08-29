package dev.toastbits.lifelog.application.settings.data.appsettings

import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.lifelog.application.settings.data.group.DatabasePreferencesGroupImpl
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup
import dev.toastbits.lifelog.application.settings.data.group.DatabaseSourcePreferencesGroupImpl
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup

class AppSettingsImpl(
    preferences: PlatformPreferences
): AppSettings {
    override val Database: DatabasePreferencesGroup = DatabasePreferencesGroupImpl(preferences)
    override val DatabaseSource: DatabaseSourcePreferencesGroup = DatabaseSourcePreferencesGroupImpl(preferences)
}
