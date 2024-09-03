package dev.toastbits.lifelog.core.filestructure

import kotlinx.serialization.Serializable

@Serializable
@Suppress("ArrayInDataClass")
data class SerialisableFileStructure(
    override val root: Node.Directory
): FileStructure {
    override val nodes: Map<String, FileStructure.Node>
        get() = root.nodes

    @Serializable
    sealed interface Node: FileStructure.Node {
        @Serializable
        data class File(
            val content: ByteArray
        ): Node, FileStructure.Node.File.FileBytes {
            override suspend fun readBytes(): Pair<ByteArray, IntRange> = content to content.indices
        }

        @Serializable
        data class Directory(
            override val nodes: MutableMap<String, Node> = mutableMapOf()
        ): Node, FileStructure.Node.Directory()
    }
}

suspend fun FileStructure.toSerialisable(onProgress: (Int) -> Unit): SerialisableFileStructure {
    if (this is SerialisableFileStructure) {
        return this
    }

    val root: SerialisableFileStructure.Node.Directory = SerialisableFileStructure.Node.Directory()
    var index: Int = 0

    onProgress(0)

    walkFiles { file, path ->
        var currentDir: SerialisableFileStructure.Node.Directory = root
        for (i in 0 until path.segments.size - 1) {
            currentDir = currentDir.nodes.getOrPut(path.segments[i]) { SerialisableFileStructure.Node.Directory() }
                as SerialisableFileStructure.Node.Directory
        }

        check(!currentDir.nodes.containsKey(path.name))

        currentDir.nodes[path.name] = SerialisableFileStructure.Node.File(file.readAllBytes())

        onProgress(++index)
    }

    return SerialisableFileStructure(root)
}
