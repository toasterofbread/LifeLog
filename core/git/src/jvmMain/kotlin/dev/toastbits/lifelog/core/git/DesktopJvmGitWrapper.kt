package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.Path
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish

class DesktopJvmGitWrapper(
    override val directory: Path,
    private val dispatcher: CoroutineDispatcher
): GitWrapper {
    private suspend fun withGit(block: (Git) -> Unit) = withContext(dispatcher) {
        block(Git.open(directory.toFile()))
    }

    override suspend fun init(): Unit = withContext(dispatcher) {
        Git.init().setDirectory(directory.toFile()).call()
    }

    override suspend fun clone(url: String): Unit = withContext(dispatcher) {
        Git.cloneRepository().setDirectory(directory.toFile()).setURI(url).call()
    }

    override suspend fun add(vararg filePatterns: String) = withGit { git ->
        git.add().apply {
            for (pattern in filePatterns) {
                addFilepattern(pattern)
            }
        }.call()
    }

    override suspend fun commit(message: String) = withGit { git ->
        git.commit().setMessage(message).call()
    }

    override suspend fun pull(remote: String?, branch: String?) = withGit { git ->
        git.pull().setRemote(remote).setRemoteBranchName(branch).call()
    }

    override suspend fun push(remote: String?) = withGit { git ->
        git.push().setRemote(remote).call()
    }

    override suspend fun remoteAdd(name: String, url: String) = withGit { git ->
        git.remoteAdd().setName(name).setUri(URIish(url)).call()
    }
}
