package dev.toastbits.lifelog.core.git.util

internal actual fun getGitBinaryFileNames(): List<String> {
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        return listOf("git.exe", "git")
    }
    return listOf("git")
}
