package dev.toastbits.lifelog.application.settings.data.compositionlocal

import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.group.DatabaseSourcePreferencesGroup

internal class AppSettingsEmpty(private val message: String): AppSettings {
    override val Database: DatabasePreferencesGroup
        get() = onAccess()

    override val DatabaseSource: DatabaseSourcePreferencesGroup
        get() = onAccess()

    private fun onAccess(): Nothing =
        throw IllegalStateException(message)
}
