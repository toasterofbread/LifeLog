package dev.toastbits.lifelog.core.git

import okio.Path

abstract class CommandLineGitWrapper: GitWrapper {
    abstract suspend fun runGitCommand(vararg args: String?): String

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
        runGitCommand("init", "--initial-branch", initialBranch)
    }

    override suspend fun clone(url: String) {
        runGitCommand("clone", addCredentialsToUrl(url), ".")
    }

    override suspend fun add(vararg filePatterns: String) {
        runGitCommand("add", *filePatterns)
    }

    override suspend fun commit(message: String) {
        runGitCommand("commit", "-m", message)
    }

    override suspend fun checkout(branch: String, createNew: Boolean) {
        if (createNew) {
            runGitCommand("checkout", "-b", branch)
        }
        else {
            runGitCommand("checkout", branch)
        }
    }

    override suspend fun checkoutOrphan(branch: String) {
        runGitCommand("checkout", "--orphan", branch)
    }

    override suspend fun fetch(remote: String?) {
        if (remote == null) {
            runGitCommand("fetch", "--all")
        }
        else {
            runGitCommand("fetch")
        }
    }

    override suspend fun pull(remote: String?, branch: String?) {
        runGitCommand("pull", remote, branch)
    }

    override suspend fun push(remote: String?, branch: String?) {
        runGitCommand("push", remote, branch)
    }

    override suspend fun remoteAdd(name: String, url: String) {
        runGitCommand("remote", "add", name, addCredentialsToUrl(url))
    }

    override suspend fun getUncommittedFiles(): List<Path> =
        runGitCommand("status", "--porcelain", "--untracked-files", "--short")
            .split("\n")
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                if (line.contains(" -> ")) {
                    return@mapNotNull null
                }
                return@mapNotNull directory.resolve(line.drop(3))
            }

    override suspend fun doesBranchExist(branch: String): Boolean {
        val branches: List<String> = runGitCommand("branch", "--all", "--format='%(refname:short)'").split("\n").filter { it.isNotBlank() }
        return branches.contains(branch)
    }
}
