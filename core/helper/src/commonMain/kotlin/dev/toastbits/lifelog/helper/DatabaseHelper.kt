package dev.toastbits.lifelog.helper

import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import dev.toastbits.lifelog.core.specification.impl.converter.LogFileConverterStringsImpl
import kotlinx.coroutines.CoroutineDispatcher

expect class DatabaseHelper(
    ioDispatcher: CoroutineDispatcher,
    splitStrategy: LogFileSplitStrategy,
    strings: LogFileConverterStrings = LogFileConverterStringsImpl()
): DatabaseHelperImpl
