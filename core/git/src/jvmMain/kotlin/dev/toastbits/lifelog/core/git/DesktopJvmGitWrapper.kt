package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class DesktopJvmGitWrapper(
    override val directory: Path,
    private val dispatcher: CoroutineDispatcher
): GitWrapper {
    private var credentials: CredentialsProvider? = null

    override fun setCredentials(credentials: GitWrapper.Credentials?) {
        this.credentials = credentials?.let {
            UsernamePasswordCredentialsProvider(it.username, it.password)
        }
    }

    private suspend fun <T> withGit(block: (Git) -> T) = withContext(dispatcher) {
        return@withContext block(Git.open(directory.toFile()))
    }

    override suspend fun init(initialBranch: String): Unit = withContext(dispatcher) {
        Git.init().setDirectory(directory.toFile()).setInitialBranch(initialBranch).call()
    }

    override suspend fun clone(url: String): Unit = withContext(dispatcher) {
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
        git.fetch().setRemote(remote).call()
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
        return@withGit (status.untracked + status.uncommittedChanges).map { it.toPath() }
    }

    override suspend fun doesBranchExist(branch: String): Boolean = withGit { git ->
        val branches: MutableList<Ref> = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
        return@withGit branches.any { it.name.split('/', limit = 3).drop(2).single() == branch }
    }
}
