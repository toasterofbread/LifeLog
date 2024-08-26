package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.util.GitConstants
import dev.toastbits.lifelog.core.git.util.indexOfOrNull
import okio.Path
import okio.Path.Companion.toPath

class GitTreeRenderer(
    private val objectRegistry: GitObjectRegistry
): GitObjectRegistry by objectRegistry {
    fun renderCommitTree(commit: GitObject, writeFile: (Path, ByteArray, IntRange) -> Unit) {
        check(commit.type == GitObject.Type.COMMIT)

        val commitContentStart: Int = commit.findContentStart()
        val treeRef: String = commit.bytes.decodeToString(commitContentStart + 5, commitContentStart + 45)

        renderTree(readObject(treeRef), writeFile)
    }

    fun renderCommitTree(commit: GitObject, fileStructure: MutableFileStructure) {
        renderCommitTree(commit) { path, bytes, range ->
            fileStructure.createFile(path, bytes, range)
        }
    }

    fun renderTree(tree: GitObject, writeFile: (Path, ByteArray, IntRange) -> Unit) {
        check(tree.type == GitObject.Type.TREE)

        var head: Int = tree.findContentStart()

        while (head < tree.bytes.size) {
            val split1: Int = tree.bytes.indexOfOrNull(' '.code.toByte(), head) ?: break
            val split2: Int = tree.bytes.indexOfOrNull(0b0, split1)!!

            val mode: String = tree.bytes.decodeToString(head, split1)
            val name: String = tree.bytes.decodeToString(split1 + 1, split2)

            head = split2 + 1

            val objRef: String = tree.bytes.toHexString(head, head + Sha1Provider.SHA1_BYTES)
            val obj: GitObject = readObject(objRef)
            head += Sha1Provider.SHA1_BYTES

            when (mode.toIntOrNull()) {
                GitConstants.TreeMode.TREE -> {
                    renderTree(obj) { path, bytes, range -> writeFile(name.toPath().resolve(path), bytes, range) }
                }
                GitConstants.TreeMode.NORMAL_FILE, GitConstants.TreeMode.EXECUTABLE_FILE -> {
                    val start: Int = obj.findContentStart()
                    writeFile(name.toPath(), obj.bytes, start until obj.bytes.size)
                }
                else -> throw NotImplementedError("$mode ($name)")
            }
        }
    }
}
