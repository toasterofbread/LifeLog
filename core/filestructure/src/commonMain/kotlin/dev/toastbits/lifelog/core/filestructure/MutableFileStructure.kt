package dev.toastbits.lifelog.core.filestructure

import okio.FileNotFoundException
import okio.FileSystem
import okio.Path

class MutableFileStructure: FileStructure {
    override val root: MutableNode.Directory = MutableNode.Directory()
    override val nodes: Map<String, FileStructure.Node> get() = root.nodes

    fun createFile(path: Path, file: FileStructure.Node.File, overwrite: Boolean = false) {
        require(path.segments.isNotEmpty())

        var currentNode: MutableNode.Directory = root
        for ((index, part) in path.segments.withIndex()) {
            if (index + 1 == path.segments.size) {
                check(overwrite || !currentNode.nodes.containsKey(part)) {
                    "Node with name '$part' already exists (${path.segments}, $index)"
                }

                currentNode.nodes[part] =
                    when (file) {
                        is FileStructure.Node.File.FileLines -> MutableNode.FileLines(file)
                        is FileStructure.Node.File.FileBytes -> MutableNode.FileBytes(file)
                    }
                continue
            }

            val pathNode: MutableNode = currentNode.nodes.getOrPut(part) { MutableNode.Directory() }
            check(pathNode is MutableNode.Directory) { "Node with name '$part' already exists (${path.segments}, $index)" }

            currentNode = pathNode
        }
    }

    fun createFile(path: Path, lines: List<String>, overwrite: Boolean = false) {
        createFile(path, FileStructure.Node.FileLinesData(lines), overwrite)
    }

    fun createFile(path: Path, bytes: ByteArray, range: IntRange = bytes.indices, overwrite: Boolean = false) {
        createFile(path, FileStructure.Node.FileBytesData(bytes, range), overwrite)
    }

    fun removeNode(path: Path) {
        if (path.segments.isEmpty()) {
            throw FileNotFoundException(path.toString())
        }

        var currentNode: MutableNode? = root

        var remainingSegments: Int = path.segments.size - 1
        while (remainingSegments > 0 && currentNode is MutableNode.Directory) {
            currentNode = currentNode.nodes[path.segments[path.segments.size - remainingSegments--]]
        }

        if (remainingSegments != 0 || currentNode !is MutableNode.Directory) {
            throw FileNotFoundException(path.toString())
        }

        if (!currentNode.nodes.containsKey(path.segments.last())) {
            throw FileNotFoundException(path.toString())
        }

        currentNode.nodes.remove(path.segments.last())
    }

    sealed interface MutableNode: FileStructure.Node {
        data class FileLines(var file: FileStructure.Node.File.FileLines): FileStructure.Node.File.FileLines,
            MutableNode {
            override suspend fun readLines(): Sequence<String> = file.readLines()
        }
        data class FileBytes(var file: FileStructure.Node.File.FileBytes): FileStructure.Node.File.FileBytes,
            MutableNode {
            override suspend fun readBytes(): Pair<ByteArray, IntRange> = file.readBytes()
        }
        data class Directory(override val nodes: MutableMap<String, MutableNode> = mutableMapOf()): FileStructure.Node.Directory(),
            MutableNode
    }

    override fun toString(): String =
        "MutableDatabaseFileStructure(nodes=$nodes)"
}
