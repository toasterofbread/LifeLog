package dev.toastbits.lifelog.core.accessor

import okio.FileSystem
import okio.Path

class MutableDatabaseFileStructure: DatabaseFileStructure {
    private val root: MutableNode.Directory = MutableNode.Directory()
    override val nodes: Map<String, DatabaseFileStructure.Node> get() = root.nodes

    fun createFile(path: Path, file: DatabaseFileStructure.Node.File) {
        require(path.segments.isNotEmpty())

        var currentNode: MutableNode.Directory = root
        for ((index, part) in path.segments.withIndex()) {
            if (index + 1 == path.segments.size) {
                check(!currentNode.nodes.containsKey(part)) { "Node with name '$part' already exists (${path.segments}, $index)" }
                currentNode.nodes[part] =
                    when (file) {
                        is DatabaseFileStructure.Node.File.FileLines -> MutableNode.FileLines(file)
                        is DatabaseFileStructure.Node.File.FileBytes -> MutableNode.FileBytes(file)
                    }
                continue
            }

            val pathNode: MutableNode = currentNode.nodes.getOrPut(part) { MutableNode.Directory() }
            check(pathNode is MutableNode.Directory) { "Node with name '$part' already exists (${path.segments}, $index)" }

            currentNode = pathNode
        }
    }

    fun createFile(path: Path, lines: List<String>) {
        createFile(path, DatabaseFileStructure.Node.FileLinesData(lines))
    }

    sealed interface MutableNode: DatabaseFileStructure.Node {
        data class FileLines(var file: DatabaseFileStructure.Node.File.FileLines): DatabaseFileStructure.Node.File.FileLines, MutableNode {
            override suspend fun readLines(fileSystem: FileSystem): Sequence<String> = file.readLines(fileSystem)
        }
        data class FileBytes(var file: DatabaseFileStructure.Node.File.FileBytes): DatabaseFileStructure.Node.File.FileBytes, MutableNode {
            override suspend fun readBytes(fileSystem: FileSystem): ByteArray = file.readBytes(fileSystem)
        }
        data class Directory(override val nodes: MutableMap<String, MutableNode> = mutableMapOf()): DatabaseFileStructure.Node.Directory(nodes), MutableNode
    }

    override fun toString(): String =
        "MutableDatabaseFileStructure(nodes=$nodes)"
}
