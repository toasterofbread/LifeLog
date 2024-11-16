package dev.toastbits.lifelog.application.settings.data.group

import dev.toastbits.composekit.platform.preferences.PlatformPreferences
import dev.toastbits.composekit.platform.preferences.impl.group.impl.ComposeKitInterfacePreferencesGroupImpl
import dev.toastbits.lifelog.application.settings.domain.group.InterfacePreferencesGroup

class InterfacePreferencesGroupImpl(
    preferences: PlatformPreferences
): ComposeKitInterfacePreferencesGroupImpl("INTERFACE", preferences), InterfacePreferencesGroup
