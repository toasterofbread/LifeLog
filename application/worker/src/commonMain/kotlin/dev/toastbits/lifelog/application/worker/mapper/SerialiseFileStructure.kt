package dev.toastbits.lifelog.application.worker.mapper

import dev.toastbits.lifelog.application.worker.model.SerialisableFileStructure
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.MutableFileStructure
import dev.toastbits.lifelog.core.filestructure.readAllBytes
import dev.toastbits.lifelog.core.filestructure.toPath
import dev.toastbits.lifelog.core.filestructure.walkFiles
import okio.Path
import okio.Path.Companion.toPath

expect suspend fun FileStructure.serialise(onProgress: (Int) -> Unit = {}): SerialisableFileStructure

expect fun SerialisableFileStructure.deserialise(): FileStructure
