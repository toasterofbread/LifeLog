package dev.toastbits.lifelog.application.settings.domain.group

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.extension.ExtensionRegistry

@Suppress("PropertyName")
interface DatabasePreferencesGroup: PreferencesGroup {
    val extensionRegistry: ExtensionRegistry
    val logFileConverterStrings: LogFileConverterStrings

    val SPLIT_STRATEGY: PreferencesProperty<LogFileSplitStrategy>
}

@Composable
fun DatabasePreferencesGroup.rememberCurrentLogDatabaseConfiguration(): LogDatabaseConfiguration {
    val splitStrategy: LogFileSplitStrategy by SPLIT_STRATEGY.observe()
    return remember(splitStrategy) {
        object : LogDatabaseConfiguration {
            override val extensionRegistry: ExtensionRegistry = this@rememberCurrentLogDatabaseConfiguration.extensionRegistry
            override val strings: LogFileConverterStrings = this@rememberCurrentLogDatabaseConfiguration.logFileConverterStrings
            override val splitStrategy: LogFileSplitStrategy = splitStrategy
        }
    }
}
