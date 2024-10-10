package dev.toastbits.lifelog.application.settings.data.group

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroupImpl
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.component.item.DropdownSettingsItem
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.lifelog.application.settings.domain.group.DisplayPreferencesGroup
import dev.toastbits.lifelog.application.settings.domain.model.DisplayDateFormat
import lifelog.application.settings.data.generated.resources.Res
import lifelog.application.settings.data.generated.resources.group_database_description
import lifelog.application.settings.data.generated.resources.group_database_title
import lifelog.application.settings.data.generated.resources.pref_display_date_format_title
import org.jetbrains.compose.resources.stringResource

class DisplayPreferencesGroupImpl(preferences: PlatformPreferences): PreferencesGroupImpl("DISPLAY", preferences), DisplayPreferencesGroup {
    @Composable
    override fun getTitle(): String = stringResource(Res.string.group_database_title)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.group_database_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.Storage

    override val DATE_FORMAT: PreferencesProperty<DisplayDateFormat> by
        enumProperty(
            getName = { stringResource(Res.string.pref_display_date_format_title) },
            getDescription = { null },
            getDefaultValue = { DisplayDateFormat.DEFAULT }
        )

    override fun getConfigurationItems(): List<SettingsItem> =
        listOf(
            DropdownSettingsItem(DATE_FORMAT) {
                it.toString()
            }
        )
}
