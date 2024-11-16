package dev.toastbits.lifelog.application.settings.data.group

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.platform.preferences.PlatformPreferences
import dev.toastbits.composekit.platform.preferences.PreferencesGroupImpl
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.settings.ui.component.item.ComposableSettingsItem
import dev.toastbits.composekit.settings.ui.component.item.DropdownSettingsItem
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.database.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.extension.ExtensionRegistry
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterStringsImpl
import dev.toastbits.lifelog.core.specification.impl.extension.ExtensionRegistryImpl
import lifelog.application.settings.data.generated.resources.Res
import lifelog.application.settings.data.generated.resources.prefs_group_database_description
import lifelog.application.settings.data.generated.resources.prefs_group_database_title
import lifelog.application.settings.data.generated.resources.pref_database_split_strategy_description
import lifelog.application.settings.data.generated.resources.pref_database_split_strategy_title
import org.jetbrains.compose.resources.stringResource

class DatabasePreferencesGroupImpl(preferences: PlatformPreferences): PreferencesGroupImpl("DATABASE_SOURCE", preferences), DatabasePreferencesGroup {
    @Composable
    override fun getTitle(): String = stringResource(Res.string.prefs_group_database_title)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.prefs_group_database_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.Storage

    override val extensionRegistry: ExtensionRegistry = ExtensionRegistryImpl()
    override val logFileConverterStrings: LogFileConverterStrings = LogFileConverterStringsImpl()

    override val SPLIT_STRATEGY: PreferencesProperty<LogFileSplitStrategy> by
        enumProperty(
            getName = { stringResource(Res.string.pref_database_split_strategy_title) },
            getDescription = { stringResource(Res.string.pref_database_split_strategy_description) },
            getDefaultValue = { LogFileSplitStrategy.Month }
        )

    override fun getConfigurationItems(): List<SettingsItem> =
        listOf(
            DropdownSettingsItem(SPLIT_STRATEGY) {
                it.toString()
            },
            ComposableSettingsItem {
                val navigator = LocalNavigator.current
                Button({
                    navigator.pushScreen(
                        object : Screen {
                            @Composable
                            override fun Content(
                                navigator: Navigator,
                                modifier: Modifier,
                                contentPadding: PaddingValues
                            ) {
                                Text("SCREEEN")
                            }
                        }
                    )
                }) {
                    Text("Click me!")
                }
            }
        )
}
