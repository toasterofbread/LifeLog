package dev.toastbits.lifelog.core.saver

import dev.toastbits.lifelog.core.saver.impl.toPath
import okio.Path
import okio.Path.Companion.toPath

interface DatabaseFileStructure {
    val nodes: Map<String, Node>

    sealed interface Node {
        open class File(open val lines: List<String>): Node {
            override fun equals(other: Any?): Boolean =
                other is File && lines == other

            override fun hashCode(): Int =
                lines.hashCode()
        }

        open class Directory(open val nodes: Map<String, Node>): Node {
            override fun equals(other: Any?): Boolean =
                other is Directory && nodes == other.nodes

            override fun hashCode(): Int =
                nodes.hashCode()
        }
    }
}

fun DatabaseFileStructure.walkFiles(onFile: (DatabaseFileStructure.Node.File, Path) -> Unit) {
    val nodes: MutableList<Pair<DatabaseFileStructure.Node, Path>> =
        nodes.map { it.value to it.key.toPath() }.toMutableList()

    while (nodes.isNotEmpty()) {
        val (node: DatabaseFileStructure.Node, path: Path) = nodes.removeLast()
        when (node) {
            is DatabaseFileStructure.Node.File -> onFile(node, path)
            is DatabaseFileStructure.Node.Directory -> {
                nodes += node.nodes.map { it.value to (path.segments + it.key).toPath() }
            }

            else -> throw IllegalStateException(node::class.toString())
        }
    }
}
