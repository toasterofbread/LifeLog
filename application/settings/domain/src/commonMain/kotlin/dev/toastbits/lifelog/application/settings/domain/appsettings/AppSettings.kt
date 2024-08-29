package dev.toastbits.lifelog.application.settings.domain.appsettings

import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup

@Suppress("PropertyName")
interface AppSettings {
    val Database: DatabasePreferencesGroup
    val DatabaseSource: DatabaseSourcePreferencesGroup
}
