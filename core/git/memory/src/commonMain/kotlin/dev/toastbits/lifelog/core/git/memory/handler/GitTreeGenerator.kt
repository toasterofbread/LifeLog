package dev.toastbits.lifelog.core.git.memory.handler

import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.filestructure.SerialisableFileStructure
import dev.toastbits.lifelog.core.git.memory.generate.generateGitObject
import dev.toastbits.lifelog.core.git.memory.model.GitObject
import dev.toastbits.lifelog.core.git.memory.model.MutableGitObjectRegistry
import dev.toastbits.lifelog.core.git.memory.provider.Sha1Provider
import dev.toastbits.lifelog.core.git.memory.util.GitConstants

class GitTreeGenerator(
    private val sha1Provider: Sha1Provider,
    private val objects: MutableGitObjectRegistry
) {
    suspend fun getOrGenerateDirectoryTree(dir: FileStructure.Node.Directory): GitObject {
        val entries: ByteArray = buildTreeEntries(dir)
        val tree: GitObject = dev.toastbits.lifelog.core.git.memory.generate.generateGitObject(
            GitObject.Type.TREE,
            entries,
            sha1Provider
        )

        objects.readObjectOrNull(tree.hash)?.also { existingTree ->
            return existingTree
        }

        objects.writeObject(tree)
        return tree
    }

    private suspend fun getOrGenerateFileBlob(file: FileStructure.Node.File): GitObject {
        val (bytes: ByteArray, bytesRange: IntRange) = getFileBytes(file)
        val hash: String = sha1Provider.calculateSha1Hash(bytes).toHexString()

        objects.readObjectOrNull(hash)?.also { existingObject ->
            check(existingObject.type == GitObject.Type.BLOB)
            return existingObject
        }

        val obj: GitObject = dev.toastbits.lifelog.core.git.memory.generate.generateGitObject(
            GitObject.Type.BLOB,
            bytes,
            sha1Provider,
            bytesRange
        )
        objects.writeObject(obj)

        return obj
    }

    private suspend fun buildTreeEntries(dir: FileStructure.Node.Directory): ByteArray {
        val parts: List<ByteArray> =
            dir.nodes.entries
                .sortedBy { (name, node) ->
                    if (node.isDir()) "$name/"
                    else name
                }
                .map { (name, node) ->
                    val obj: GitObject = node.getObject()
                    "${node.getMode()} $name\u0000".encodeToByteArray() + obj.hash.hexToByteArray()
                }

        val bytes: ByteArray = ByteArray(parts.sumOf { it.size })
        var head: Int = 0
        for (part in parts) {
            part.copyInto(bytes, head)
            head += part.size
        }

        return bytes
    }

    private fun FileStructure.Node.getMode(): Int =
        if (isDir()) GitConstants.TreeMode.TREE
        else GitConstants.TreeMode.NORMAL_FILE

    // The compiler doesn't need the explicit casts, but AS lint does for some reason
    private suspend fun FileStructure.Node.getObject(): GitObject =
        if (isDir()) getOrGenerateDirectoryTree(this as FileStructure.Node.Directory)
        else getOrGenerateFileBlob(this as FileStructure.Node.File)

    private fun FileStructure.Node.isDir(): Boolean =
        when (this) {
            is FileStructure.Node.File,
            is SerialisableFileStructure.Node.File,
            is MutableFileStructure.MutableNode.FileBytes,
            is MutableFileStructure.MutableNode.FileLines -> false
            is FileStructure.Node.Directory,
            is SerialisableFileStructure.Node.Directory,
            is MutableFileStructure.MutableNode.Directory -> true
        }

    private suspend fun getFileBytes(file: FileStructure.Node.File): Pair<ByteArray, IntRange> =
        when (file) {
            is FileStructure.Node.File.FileBytes ->
                file.readBytes()
            is FileStructure.Node.File.FileLines ->
                file.readLines().joinToString("\n").encodeToByteArray().let { it to it.indices }
        }
}
