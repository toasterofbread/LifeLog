package dev.toastbits.lifelog.application.worker.mapper

import dev.toastbits.lifelog.application.worker.model.SerialisableFileStructure
import dev.toastbits.lifelog.core.filestructure.FileStructure

actual suspend fun FileStructure.serialise(onProgress: (Int) -> Unit): SerialisableFileStructure =
    SerialisableFileStructure(this)

actual fun SerialisableFileStructure.deserialise(): FileStructure =
    fileStructure
