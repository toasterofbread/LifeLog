package dev.toastbits.lifelog.core.git.util

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
actual fun runCommand(program: String, vararg args: String?): String? {
    val command: String =
        buildString {
            append("\"$program\"")

            for (arg in args) {
                if (arg == null) {
                    continue
                }

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

actual fun runCommandWithInput(
    input: String,
    program: String,
    vararg args: String?
): String? {
    TODO("Not yet implemented")
}