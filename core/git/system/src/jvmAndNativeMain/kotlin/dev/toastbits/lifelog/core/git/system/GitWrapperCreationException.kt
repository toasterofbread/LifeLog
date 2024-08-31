package dev.toastbits.lifelog.core.git.system

sealed class GitWrapperCreationException(message: String? = null): Throwable(message) {
    class GitBinaryNotFunctional(val gitBinaryPath: String): GitWrapperCreationException(gitBinaryPath)
    class EnvironmentPathNotFound: GitWrapperCreationException()
    class GitNotFoundInEnvironmentPath: GitWrapperCreationException()
}
