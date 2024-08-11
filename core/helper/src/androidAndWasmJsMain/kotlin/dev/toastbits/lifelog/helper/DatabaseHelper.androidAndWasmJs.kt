package dev.toastbits.lifelog.helper

import dev.toastbits.lifelog.core.accessor.LogFileSplitStrategy
import dev.toastbits.lifelog.core.specification.converter.LogFileConverterStrings
import kotlinx.coroutines.CoroutineDispatcher

actual class DatabaseHelper actual constructor(
    ioDispatcher: CoroutineDispatcher,
    splitStrategy: LogFileSplitStrategy,
    strings: LogFileConverterStrings
) : DatabaseHelperImpl(ioDispatcher, splitStrategy, strings) {

}