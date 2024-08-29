package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.extension.ExtensionRegistry

interface LogDatabaseConfiguration {
    val extensionRegistry: ExtensionRegistry
    val splitStrategy: LogFileSplitStrategy
    val strings: LogFileConverterStrings
}
