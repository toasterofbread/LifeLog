package dev.toastbits.lifelog.application.settings.domain.appsettings

import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DisplayPreferencesGroup

@Suppress("PropertyName")
interface AppSettings {
    val allGroups: List<PreferencesGroup> get() =
        listOf(Database, DatabaseSource)

    val prefs: PlatformPreferences

    val Database: DatabasePreferencesGroup
    val DatabaseSource: DatabaseSourcePreferencesGroup
    val Display: DisplayPreferencesGroup
}
