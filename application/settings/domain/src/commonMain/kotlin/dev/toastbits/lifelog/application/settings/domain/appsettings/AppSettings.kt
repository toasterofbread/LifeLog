package dev.toastbits.lifelog.application.settings.domain.appsettings

import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DisplayPreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.InterfacePreferencesGroup

@Suppress("PropertyName")
interface AppSettings {
    val allGroups: List<PreferencesGroup> get() =
        listOf(
            Database,
            DatabaseSource,
            Interface,
            Display
        )

    val prefs: PlatformPreferences

    val Database: DatabasePreferencesGroup
    val DatabaseSource: DatabaseSourcePreferencesGroup
    val Interface: InterfacePreferencesGroup
    val Display: DisplayPreferencesGroup
}
