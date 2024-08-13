package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.Path
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class DesktopJvmGitWrapper(
    override val directory: Path,
    private val ioDispatcher: CoroutineDispatcher
): GitWrapper {
    private var credentials: CredentialsProvider? = null

    override fun setCredentials(credentials: GitWrapper.Credentials?) {
        this.credentials = credentials?.let {
            UsernamePasswordCredentialsProvider(it.username, it.password)
        }
    }

    private suspend fun <T> withGit(block: (Git) -> T) = withContext(ioDispatcher) {
        return@withContext block(Git.open(directory.toFile()))
    }

    override suspend fun init(initialBranch: String): Unit = withContext(ioDispatcher) {
        Git.init().setDirectory(directory.toFile()).setInitialBranch(initialBranch).call()
    }

    override suspend fun clone(url: String): Unit = withContext(ioDispatcher) {
        Git.cloneRepository().setDirectory(directory.toFile()).setURI(url).call()
    }

    override suspend fun add(vararg filePatterns: String): Unit = withGit { git ->
        git.add().apply {
            for (pattern in filePatterns) {
                addFilepattern(pattern)
            }
        }.call()
    }

    override suspend fun commit(message: String): Unit = withGit { git ->
        git.commit().setCredentialsProvider(credentials).setMessage(message).call()
    }

    override suspend fun checkout(branch: String, createNew: Boolean): Unit = withGit { git ->
        git.checkout().setName(branch).setCreateBranch(createNew).call()
    }

    override suspend fun checkoutOrphan(branch: String): Unit = withGit { git ->
        git.checkout().setOrphan(true).setName(branch).call()
    }

    override suspend fun fetch(remote: String?): Unit = withGit { git ->
        git.fetch().setCredentialsProvider(credentials).setRemote(remote).call()
    }

    override suspend fun pull(remote: String?, branch: String?): Unit = withGit { git ->
        git.pull().setCredentialsProvider(credentials).setRemote(remote).setRemoteBranchName(branch).call()
    }

    override suspend fun push(remote: String?, branch: String?): Unit = withGit { git ->
        git.push().setCredentialsProvider(credentials).setRemote(remote).setPushOptions(listOfNotNull(branch)).call()
    }

    override suspend fun remoteAdd(name: String, url: String): Unit = withGit { git ->
        git.remoteAdd().setName(name).setUri(URIish(url)).call()
    }

    override suspend fun getUncommittedFiles(): List<Path> = withGit { git ->
        val status: Status = git.status().call()
        return@withGit (status.changed + status.uncommittedChanges).map { directory.resolve(it) }
    }

    override suspend fun doesBranchExist(branch: String): Boolean = withGit { git ->
        return@withGit git.getBranches().containsKey(branch)
    }

    private fun Git.getBranches(): Map<String, Ref> =
        branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
            .filter { it.name != "HEAD" }
            .associateBy { branch ->
                try {
                    branch.name.split('/', limit = 3).drop(2).single()
                }
                catch (e: Throwable) {
                    throw RuntimeException(branch.name, e)
                }
            }
}
