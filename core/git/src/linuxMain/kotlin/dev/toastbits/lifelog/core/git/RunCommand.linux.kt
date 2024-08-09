package dev.toastbits.lifelog.core.git

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.FILE
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

@OptIn(ExperimentalForeignApi::class)
actual fun runCommand(program: String, args: List<String>): String? {
    val command: String =
        buildString {
            append(program.replace(" ", "\\ "))

            for (arg in args) {
                append(' ')

                if (arg.contains(" ")) {
                    append("\"$arg\"")
                }
                else {
                    append(arg)
                }
            }
        }

    println(command)

    val process: CPointer<FILE>? = popen(command, "r")

    val output: String = buildString {
        val buffer: ByteArray = ByteArray(4096)
        while (true) {
            val input: CPointer<ByteVarOf<Byte>> = fgets(buffer.refTo(0), buffer.size, process) ?: break
            append(input.toKString())
        }
    }

    if (pclose(process) != 0) {
        return null
    }

    return output
}
