package dev.toastbits.lifelog.core.git.util

import dev.toastbits.lifelog.core.git.GitWrapperCreationException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

fun getGitBinaryPath(): String {
    getEnv(GIT_BINARY_PATH_OVERRIDE_ENV_VAR)?.also { return it }

    val path: String =
        getEnv("PATH") ?: throw GitWrapperCreationException.EnvironmentPathNotFound()

    for (location in path.split(':')) {
        for (fileName in getGitBinaryFileNames()) {
            val gitBinaryPath: Path = location.toPath().resolve(fileName)
            if (FileSystem.SYSTEM.exists(gitBinaryPath)) {
                return gitBinaryPath.toString()
            }
        }
    }

    throw GitWrapperCreationException.GitNotFoundInEnvironmentPath()
}

const val GIT_BINARY_PATH_OVERRIDE_ENV_VAR: String = "GIT_PATH"

internal expect fun getGitBinaryFileNames(): List<String>
