package dev.toastbits.lifelog.core.git

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.FILE
import platform.posix._pclose
import platform.posix._popen

@OptIn(ExperimentalForeignApi::class)
actual fun runCommand(program: String, args: List<String>): Int {
    val command: String =
        buildString {
            append("\"$program\"")

            for (arg in args) {
                append(' ')
                append("\"$arg\"")
            }
        }

    val process: CPointer<FILE>? = _popen("\"$command\"", "r")
    val result: Int = _pclose(process)
    if (result == 141) {
        return 0
    }
    return result
}