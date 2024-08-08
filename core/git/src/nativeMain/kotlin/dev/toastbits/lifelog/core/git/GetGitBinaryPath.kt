package dev.toastbits.lifelog.core.git

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
fun getGitBinaryPath(): String {
    getenv(GIT_BINARY_PATH_OVERRIDE_ENV_VAR)?.toKString()?.also { return it }

    val path: String =
        getenv("PATH")?.toKString()
            ?: throw GitWrapperCreationException.EnvironmentPathNotFound()

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
