package dev.toastbits.lifelog.application.settings.domain.group

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

suspend fun DatabasePreferencesGroup.getLogDatabaseConfiguration(): LogDatabaseConfiguration {
    val splitStrategy: LogFileSplitStrategy = SPLIT_STRATEGY.get()
    return LogDatabaseConfigurationImpl(
        extensionRegistry = extensionRegistry,
        splitStrategy = splitStrategy,
        strings = logFileConverterStrings
        )
}

private data class LogDatabaseConfigurationImpl(
    override val extensionRegistry: ExtensionRegistry,
    override val splitStrategy: LogFileSplitStrategy,
    override val strings: LogFileConverterStrings
) : LogDatabaseConfiguration
