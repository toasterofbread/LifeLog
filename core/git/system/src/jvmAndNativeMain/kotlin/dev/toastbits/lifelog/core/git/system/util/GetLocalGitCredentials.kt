package dev.toastbits.lifelog.core.git.system.util

import dev.toastbits.lifelog.core.git.system.GitWrapper
import dev.toastbits.lifelog.core.git.system.getAndCheckGitBinaryPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

suspend fun getLocalGitCredentials(url: String, repositoryPath: String): GitWrapper.Credentials? = withContext(Dispatchers.IO) {
    val gitBinaryPath: String =
        try {
            getAndCheckGitBinaryPath()
        }
        catch (_: Throwable) {
            return@withContext null
        }

    val result: String =
        runCommandWithInput("url=$url", gitBinaryPath, "-C", repositoryPath, "credential", "fill")
        ?: return@withContext null

    val values: Map<String, String> =
        try {
            result.split('\n').filter { it.isNotBlank() }.associate { line -> line.split('=', limit = 2).let { it[0] to it[1] } }
        }
        catch (e: Throwable) {
            throw RuntimeException("Result parsing failed (using $gitBinaryPath and $url): $result", e)
        }

    val username: String = values["username"] ?: return@withContext null
    val password: String = values["password"] ?: return@withContext null

    return@withContext GitWrapper.Credentials(username = username, password = password)
}
