package dev.toastbits.lifelog.core.filestructure

import okio.Path
import okio.Path.Companion.toPath

interface FileStructure {
    val root: Node.Directory
    val nodes: Map<String, Node>

    sealed interface Node {
        sealed interface File: Node {
            interface FileLines: File {
                suspend fun readLines(): Sequence<String>
            }

            interface FileBytes: File {
                suspend fun readBytes(): Pair<ByteArray, IntRange>
            }
        }

        class FileLinesData(private val lines: List<String>): File.FileLines {
            override suspend fun readLines(): Sequence<String> = lines.asSequence()
        }

        class FileBytesData(private val bytes: ByteArray, private val range: IntRange = bytes.indices): File.FileBytes {
            override suspend fun readBytes(): Pair<ByteArray, IntRange> = bytes to range
        }

        abstract class Directory: Node {
            abstract val nodes: Map<String, Node>

            override fun equals(other: Any?): Boolean =
                other is Directory && nodes == other.nodes

            override fun hashCode(): Int =
                nodes.hashCode()
        }

        data class DirectoryData(override val nodes: Map<String, Node>) : Directory()
    }
}

inline fun FileStructure.Node.walkFiles(onFile: (FileStructure.Node.File, Path) -> Unit) {
    val nodes: MutableList<Pair<FileStructure.Node, Path>> = mutableListOf(this to "".toPath())

    while (nodes.isNotEmpty()) {
        val (node: FileStructure.Node, path: Path) = nodes.removeLast()
        when (node) {
            is FileStructure.Node.File -> onFile(node, path)
            is FileStructure.Node.Directory -> {
                nodes += node.nodes.map { it.value to (path.segments + it.key).toPath() }
            }

            else -> throw IllegalStateException(node::class.toString())
        }
    }
}

fun FileStructure.countFiles(): Int =
    nodes.values.sumOf { it.countFiles() }

fun FileStructure.Node.countFiles(): Int =
    when (this) {
        is FileStructure.Node.File -> 1
        is FileStructure.Node.Directory -> nodes.values.sumOf { it.countFiles() }
        else -> throw IllegalStateException(this::class.toString())
    }

inline fun FileStructure.walkFiles(onFile: (FileStructure.Node.File, Path) -> Unit) {
    for ((name, node) in nodes) {
        node.walkFiles { file, path ->
            onFile(file, name.toPath().resolve(path))
        }
    }
}

fun FileStructure.getOrNull(path: Path): FileStructure.Node? {
    if (path.segments.isEmpty()) {
        return null
    }

    var remainingSegments: Int = path.segments.size - 1
    var currentNode: FileStructure.Node? = nodes[path.segments.first()]
    while (remainingSegments > 0 && currentNode is FileStructure.Node.Directory) {
        currentNode = currentNode.nodes[path.segments[path.segments.size - remainingSegments--]]
    }

    if (remainingSegments != 0) {
        return null
    }

    return currentNode
}

fun List<String>.toPath(): Path =
    joinToString(Path.DIRECTORY_SEPARATOR).toPath()

suspend fun FileStructure.Node.File.readAllBytes(): ByteArray =
    when (this) {
        is FileStructure.Node.File.FileBytes -> this.readBytes().let { (bytes, region) -> bytes.sliceArray(region) }
        is FileStructure.Node.File.FileLines -> this.readLines().joinToString("\n").encodeToByteArray()
    }

suspend fun FileStructure.Node.File.readLines(): Sequence<String> =
    when (this) {
        is FileStructure.Node.File.FileBytes -> this.readBytes().let { (bytes, region) -> bytes.decodeToString(region.first, region.last + 1) }.splitToSequence("\n")
        is FileStructure.Node.File.FileLines -> this.readLines()
    }

