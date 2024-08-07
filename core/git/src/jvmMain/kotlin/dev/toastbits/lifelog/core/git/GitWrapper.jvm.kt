package dev.toastbits.lifelog.core.git

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import java.io.File

actual class GitWrapper actual constructor(
    private val directory: File,
    private val dispatcher: CoroutineDispatcher
) {
    private suspend fun withGit(block: (Git) -> Unit) = withContext(dispatcher) {
        block(Git.open(directory))
    }

    actual suspend fun init(): Unit = withContext(dispatcher) {
        Git.init().setDirectory(directory).call()
    }

    actual suspend fun clone(url: String): Unit = withContext(dispatcher) {
        Git.cloneRepository().setDirectory(directory).setURI(url).call()
    }

    actual suspend fun add(vararg filePatterns: String) = withGit { git ->
        git.add().apply {
            for (pattern in filePatterns) {
                addFilepattern(pattern)
            }
        }.call()
    }

    actual suspend fun commit(message: String) = withGit { git ->
        git.commit().setMessage(message).call()
    }

    actual suspend fun pull(remote: String?) = withGit { git ->
        git.pull().setRemote(remote).call()
    }

    actual suspend fun push(remote: String?) = withGit { git ->
        git.push().setRemote(remote).call()
    }

    actual suspend fun remoteAdd(name: String, url: String) = withGit { git ->
        git.remoteAdd().setName(name).setUri(URIish(url)).call()
    }
}
