package dev.toastbits.lifelog.application.settings.data.group

import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroupImpl
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.lifelog.application.settings.domain.group.DatabasePreferencesGroup
import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.extension.ExtensionRegistry
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterStringsImpl
import dev.toastbits.lifelog.core.specification.impl.extension.ExtensionRegistryImpl
import lifelog.application.settings.data.generated.resources.Res
import lifelog.application.settings.data.generated.resources.pref_database_split_strategy_title
import lifelog.application.settings.data.generated.resources.pref_database_split_strategy_description
import org.jetbrains.compose.resources.stringResource

class DatabasePreferencesGroupImpl(preferences: PlatformPreferences): PreferencesGroupImpl("DATABASE_SOURCE", preferences), DatabasePreferencesGroup {
    override val extensionRegistry: ExtensionRegistry = ExtensionRegistryImpl()
    override val logFileConverterStrings: LogFileConverterStrings = LogFileConverterStringsImpl()

    override val SPLIT_STRATEGY: PreferencesProperty<LogFileSplitStrategy> by
        enumProperty(
            getName = { stringResource(Res.string.pref_database_split_strategy_title) },
            getDescription = { stringResource(Res.string.pref_database_split_strategy_description) },
            getDefaultValue = { LogFileSplitStrategy.Month }
        )
}
