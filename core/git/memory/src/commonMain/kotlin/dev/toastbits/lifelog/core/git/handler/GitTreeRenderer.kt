package dev.toastbits.lifelog.core.git.handler

import dev.toastbits.lifelog.core.git.model.GitObject
import dev.toastbits.lifelog.core.git.model.GitObjectRegistry
import dev.toastbits.lifelog.core.git.util.indexOfOrNull
import okio.FileSystem
import okio.Path

class GitTreeRenderer(
    private val objectRegistry: GitObjectRegistry,
    private val fileSystem: FileSystem
): GitObjectRegistry by objectRegistry {
    fun renderCommit(commit: GitObject, path: Path) {
        check(commit.type == GitObject.Type.COMMIT)

        val commitContentStart: Int = commit.findContentStart()
        val treeRef: String = commit.bytes.decodeToString(commitContentStart + 5, commitContentStart + 45)

        renderTree(readObject(treeRef), path)
    }

    fun renderTree(tree: GitObject, path: Path) {
        check(tree.type == GitObject.Type.TREE)

        val bytes: ByteArray = tree.bytes
        var head: Int = tree.findContentStart()

        fileSystem.createDirectories(path)
        check(fileSystem.exists(path))

        while (head < bytes.size) {
            val split1: Int = bytes.indexOfOrNull(' '.code.toByte(), head) ?: break
            val split2: Int = bytes.indexOfOrNull(0b0, split1)!!

            val mode: String = bytes.decodeToString(head, split1)
            val name: String = bytes.decodeToString(split1 + 1, split2)

            head = split2 + 1

            val objRef: String = bytes.toHexString(head, head + 20)
            val obj: GitObject = readObject(objRef)
            head += 20

            when (mode) {
                "40000" -> {
                    renderTree(obj, path.resolve(name))
                }
                "100644", "100755" -> {
                    fileSystem.write(path.resolve(name)) {
                        val start: Int = obj.findContentStart()
                        write(obj.bytes, start, obj.bytes.size - start)
                    }
                }
                else -> throw NotImplementedError("$mode ($name)")
            }
        }
    }
}
