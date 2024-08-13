package dev.toastbits.lifelog.core.git

import dev.toastbits.lifelog.core.git.util.runCommand
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path

class CommandLineGitWrapper(
    private val gitBinaryPath: String,
    override val directory: Path,
    private val fileSystem: FileSystem,
    private val ioDispatcher: CoroutineDispatcher
): GitWrapper {
    private suspend fun runGitCommandInDirectory(vararg args: String?): String = withContext(ioDispatcher) {
        if (!fileSystem.exists(directory)) {
            fileSystem.createDirectories(directory, mustCreate = true)
        }

        val finalArgs: Array<String> = arrayOf("-C", directory.toString()) + args.filterNotNull()

        val output: String? = runCommand(gitBinaryPath, *finalArgs)
        checkNotNull(output) { "Running $gitBinaryPath with ${args.toList()} failed" }

        return@withContext output
    }

    private var credentials: GitWrapper.Credentials? = null
    private fun addCredentialsToUrl(url: String): String {
        val (username: String, password: String) = credentials ?: return url

        if (url.startsWith("https://")) {
            return "https://$username:$password@${url.drop(8)}"
        }

        throw NotImplementedError("Unknown url format: $url")
    }

    override fun setCredentials(credentials: GitWrapper.Credentials?) {
        this.credentials = credentials
    }

    override suspend fun init(initialBranch: String) {
        runGitCommandInDirectory("init", "--initial-branch", initialBranch)
    }

    override suspend fun clone(url: String) {
        runGitCommandInDirectory("clone", addCredentialsToUrl(url), ".")
    }

    override suspend fun add(vararg filePatterns: String) {
        runGitCommandInDirectory("add", *filePatterns)
    }

    override suspend fun commit(message: String) {
        runGitCommandInDirectory("commit", "-m", message)
    }

    override suspend fun checkout(branch: String, createNew: Boolean) {
        if (createNew) {
            runGitCommandInDirectory("checkout", "-b", branch)
        }
        else {
            runGitCommandInDirectory("checkout", branch)
        }
    }

    override suspend fun checkoutOrphan(branch: String) {
        runGitCommandInDirectory("checkout", "--orphan", branch)
    }

    override suspend fun fetch(remote: String?) {
        if (remote == null) {
            runGitCommandInDirectory("fetch", "--all")
        }
        else {
            runGitCommandInDirectory("fetch")
        }
    }

    override suspend fun pull(remote: String?, branch: String?) {
        runGitCommandInDirectory("pull", remote, branch)
    }

    override suspend fun push(remote: String?, branch: String?) {
        runGitCommandInDirectory("push", remote, branch)
    }

    override suspend fun remoteAdd(name: String, url: String) {
        runGitCommandInDirectory("remote", "add", name, addCredentialsToUrl(url))
    }

    override suspend fun getUncommittedFiles(): List<Path> =
        runGitCommandInDirectory("status", "--porcelain", "--untracked-files", "--short")
            .split("\n")
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                if (line.contains(" -> ")) {
                    return@mapNotNull null
                }
                return@mapNotNull directory.resolve(line.drop(3))
            }

    override suspend fun doesBranchExist(branch: String): Boolean {
        val branches: List<String> =
            runGitCommandInDirectory("branch", "--all", "--format='%(refname:short)'")
                .split("\n")
                .filter { it.isNotBlank() }
                .map { it.trim('\'') }
        return branches.contains(branch)
    }
}
