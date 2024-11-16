package dev.toastbits.lifelog.application.settings.domain.appsettings

import dev.toastbits.composekit.platform.preferences.PlatformPreferences
import dev.toastbits.composekit.platform.preferences.PreferencesGroup
import dev.toastbits.composekit.platform.preferences.impl.ComposeKitSettings
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DisplayPreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.InterfacePreferencesGroup

@Suppress("PropertyName")
interface AppSettings: ComposeKitSettings {
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
    override val Interface: InterfacePreferencesGroup
    val Display: DisplayPreferencesGroup
}
