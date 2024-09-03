package dev.toastbits.lifelog.application.worker.mapper

import dev.toastbits.lifelog.application.worker.model.TransferableFileStructure
import dev.toastbits.lifelog.core.filestructure.FileStructure

actual suspend fun FileStructure.toTransferable(onProgress: (Int) -> Unit): TransferableFileStructure =
    TransferableFileStructure(this)

actual fun TransferableFileStructure.deserialise(): FileStructure = this
