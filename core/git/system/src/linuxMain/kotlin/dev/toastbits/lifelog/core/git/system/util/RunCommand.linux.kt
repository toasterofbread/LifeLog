package dev.toastbits.lifelog.core.git.system.util

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CValues
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import platform.posix.close
import platform.posix.read
import platform.posix.waitpid
import platform.posix.write
import popen2.popen2
import popen2.popen2_data

actual fun runCommand(program: String, vararg args: String?): String? =
    runProcessCommand(program, *args)

actual fun runCommandWithInput(
    input: String,
    program: String,
    vararg args: String?
): String? =
    runProcessCommand(program, *args) { process ->
        val inputString: CValues<ByteVarOf<Byte>> = input.cstr
        write(process.to_child, inputString, inputString.size.toULong())
        close(process.to_child)
    }

private fun runProcessCommand(
    program: String,
    vararg args: String?,
    duringRun: (popen2_data) -> Unit = {}
): String? = memScoped {
    val command: String =
        buildString {
            append(program.replace(" ", "\\ "))

            for (arg in args) {
                if (arg == null) {
                    continue
                }

                append(' ')

                if (arg.contains(" ")) {
                    append("\"$arg\"")
                }
                else {
                    append(arg)
                }
            }
        }

    val process: popen2_data = alloc()
    popen2(command, process.ptr)

    duringRun(process)

    val output: String =
        buildString {
            val buffer: ByteArray = ByteArray(4096)
            while (true) {
                val size: Long = read(process.from_child, buffer.refTo(0), buffer.size.toULong())
                if (size <= 0) {
                    break
                }
                append(buffer.decodeToString(0, size.toInt()))
            }
        }

    val status: IntVar = alloc()
    waitpid(process.child_pid, status.ptr, 0)

//    println("CMD $command -> $output (${status.value})")

    if (status.value != 0) {
        return null
    }

    return output
}
