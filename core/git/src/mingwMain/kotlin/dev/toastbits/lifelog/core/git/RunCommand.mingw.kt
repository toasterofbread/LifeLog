package dev.toastbits.lifelog.core.git

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.FILE
import platform.posix._pclose
import platform.posix._popen
import platform.posix.fgets

@OptIn(ExperimentalForeignApi::class)
actual fun runCommand(program: String, args: List<String>): String? {
    val command: String =
        buildString {
            append("\"$program\"")

            for (arg in args) {
                append(' ')
                append("\"$arg\"")
            }
        }

    val process: CPointer<FILE>? = _popen("\"$command\"", "r")
    val output: String = buildString {
        val buffer: ByteArray = ByteArray(4096)
        while (true) {
            val input: CPointer<ByteVarOf<Byte>> = fgets(buffer.refTo(0), buffer.size, process) ?: break
            append(input.toKString())
        }
    }

    val result: Int = _pclose(process)
    if (result != 0 && result != 141) {
        return null
    }

    return output
}
