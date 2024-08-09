package dev.toastbits.lifelog.core.saver

import okio.Path

internal class MutableDatabaseFileStructure: DatabaseFileStructure {
    private val root: MutableNode.Directory = MutableNode.Directory()
    override val nodes: Map<String, DatabaseFileStructure.Node> get() = root.nodes

    fun createFile(path: Path, lines: List<String>) {
        require(path.segments.isNotEmpty())

        var currentNode: MutableNode.Directory = root
        for ((index, part) in path.segments.withIndex()) {
            if (index + 1 == path.segments.size) {
                check(!currentNode.nodes.containsKey(part)) { "Node with name '$part' already exists (${path.segments}, $index)" }
                currentNode.nodes[part] = MutableNode.File(lines)
                continue
            }

            val pathNode: MutableNode = currentNode.nodes.getOrPut(part) { MutableNode.Directory() }
            check(pathNode is MutableNode.Directory) { "Node with name '$part' already exists (${path.segments}, $index)" }

            currentNode = pathNode
        }
    }

    sealed interface MutableNode: DatabaseFileStructure.Node {
        data class File(override var lines: List<String> = emptyList()): DatabaseFileStructure.Node.File(lines), MutableNode
        data class Directory(override val nodes: MutableMap<String, MutableNode> = mutableMapOf()): DatabaseFileStructure.Node.Directory(nodes), MutableNode
    }
}
