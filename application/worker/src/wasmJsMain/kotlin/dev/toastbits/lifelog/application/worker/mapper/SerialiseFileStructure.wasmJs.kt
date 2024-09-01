package dev.toastbits.lifelog.application.worker.mapper

import dev.toastbits.lifelog.application.worker.model.SerialisableFileStructure
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.filestructure.readAllBytes
import dev.toastbits.lifelog.core.filestructure.toPath
import dev.toastbits.lifelog.core.filestructure.walkFiles
import okio.Path
import okio.Path.Companion.toPath

actual suspend fun FileStructure.serialise(onProgress: (Int) -> Unit): SerialisableFileStructure {
    val root: SerialisableFileStructure.Node.Directory = SerialisableFileStructure.Node.Directory()
    var index: Int = 0

    onProgress(0)

    walkFiles { file, path ->
        var currentDir: SerialisableFileStructure.Node.Directory = root
        for (i in 0 until path.segments.size - 1) {
            currentDir =
                currentDir.nodes.getOrPut(path.segments[i]) { SerialisableFileStructure.Node.Directory() }
                as SerialisableFileStructure.Node.Directory
        }

        check(!currentDir.nodes.containsKey(path.name))

        currentDir.nodes[path.name] = SerialisableFileStructure.Node.File(file.readAllBytes())

        onProgress(++index)
    }

    return SerialisableFileStructure(root)
}

actual fun SerialisableFileStructure.deserialise(): FileStructure {
    val structure: MutableFileStructure = MutableFileStructure()
    root.walkFiles { file, path ->
        structure.createFile(path, file.content)
    }
    return structure
}

private fun SerialisableFileStructure.Node.walkFiles(onFile: (SerialisableFileStructure.Node.File, Path) -> Unit) {
    val nodes: MutableList<Pair<SerialisableFileStructure.Node, Path>> = mutableListOf(this to "".toPath())

    while (nodes.isNotEmpty()) {
        val (node: SerialisableFileStructure.Node, path: Path) = nodes.removeLast()
        when (node) {
            is SerialisableFileStructure.Node.File -> onFile(node, path)
            is SerialisableFileStructure.Node.Directory -> {
                nodes += node.nodes.map { it.value to (path.segments + it.key).toPath() }
            }
        }
    }
}
