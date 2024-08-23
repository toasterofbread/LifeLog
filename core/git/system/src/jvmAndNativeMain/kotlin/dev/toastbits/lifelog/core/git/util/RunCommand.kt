package dev.toastbits.lifelog.core.git.util

expect fun runCommand(program: String, vararg args: String?): String?

expect fun runCommandWithInput(input: String, program: String, vararg args: String?): String?
