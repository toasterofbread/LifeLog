package dev.toastbits.lifelog.core.git

abstract class CommandLineGitWrapper: GitWrapper {
    abstract suspend fun runGitCommand(vararg args: String?)

    override suspend fun init() {
        runGitCommand("init")
    }

    override suspend fun clone(url: String) {
        runGitCommand("clone", url, ".")
    }

    override suspend fun add(vararg filePatterns: String) {
        runGitCommand("add", *filePatterns)
    }

    override suspend fun commit(message: String) {
        runGitCommand("commit", "-m", message)
    }

    override suspend fun pull(remote: String?, branch: String?) {
        runGitCommand("pull", remote, branch)
    }

    override suspend fun push(remote: String?) {
        runGitCommand("push", remote)
    }

    override suspend fun remoteAdd(name: String, url: String) {
        runGitCommand("remote", "add", name, url)
    }
}
