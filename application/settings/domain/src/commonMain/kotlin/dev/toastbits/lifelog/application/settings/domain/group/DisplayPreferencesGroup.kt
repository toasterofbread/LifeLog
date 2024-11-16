package dev.toastbits.lifelog.application.settings.domain.group

import dev.toastbits.composekit.platform.preferences.PreferencesGroup
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.lifelog.application.settings.domain.model.DisplayDateFormat

@Suppress("PropertyName")
interface DisplayPreferencesGroup: PreferencesGroup {
    val DATE_FORMAT: PreferencesProperty<DisplayDateFormat>
}
