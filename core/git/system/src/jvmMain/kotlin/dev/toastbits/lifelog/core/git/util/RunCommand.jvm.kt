package dev.toastbits.lifelog.core.git.util

import java.io.BufferedReader

actual fun runCommand(program: String, vararg args: String?): String? =
    runProcessCommand(program, *args)

actual fun runCommandWithInput(
    input: String,
    program: String,
    vararg args: String?
): String? =
    runProcessCommand(program, *args) { process ->
        process.outputStream.writer().use { writer ->
            writer.write(input)
        }
    }

private fun runProcessCommand(program: String, vararg args: String?, duringRun: (Process) -> Unit = {}): String? {
    val process: Process = ProcessBuilder(program, *args).start()
    duringRun(process)

    val stdout: BufferedReader = process.inputStream.bufferedReader()
    val output: String =
        buildString {
            while (true) {
                appendLine(stdout.readLine() ?: break)
            }
        }

    val result: Int = process.waitFor()
    if (result != 0) {
        return null
    }

    return output
}
