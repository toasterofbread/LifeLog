package dev.toastbits.lifelog.core.accessor

import dev.toastbits.lifelog.core.accessor.impl.toPath
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

interface DatabaseFileStructure {
    val nodes: Map<String, Node>

    sealed interface Node {
        interface File: Node {
            suspend fun readLines(fileSystem: FileSystem): Sequence<String>
        }

        data class FileData(val lines: List<String>): File {
            override suspend fun readLines(fileSystem: FileSystem): Sequence<String> = lines.asSequence()
        }

        open class Directory(open val nodes: Map<String, Node>): Node {
            override fun equals(other: Any?): Boolean =
                other is Directory && nodes == other.nodes

            override fun hashCode(): Int =
                nodes.hashCode()
        }
    }
}

inline fun DatabaseFileStructure.Node.walkFiles(onFile: (DatabaseFileStructure.Node.File, Path) -> Unit) {
    val nodes: MutableList<Pair<DatabaseFileStructure.Node, Path>> = mutableListOf(this to "".toPath())

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

inline fun DatabaseFileStructure.walkFiles(onFile: (DatabaseFileStructure.Node.File, Path) -> Unit) {
    for ((name, node) in nodes) {
        node.walkFiles { file, path ->
            onFile(file, name.toPath().resolve(path))
        }
    }
}
