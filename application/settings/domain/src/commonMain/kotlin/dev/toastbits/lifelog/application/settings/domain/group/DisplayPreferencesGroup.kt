package dev.toastbits.lifelog.application.settings.domain.group

import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.lifelog.application.settings.domain.model.DisplayDateFormat

@Suppress("PropertyName")
interface DisplayPreferencesGroup: PreferencesGroup {
    val DATE_FORMAT: PreferencesProperty<DisplayDateFormat>
}
