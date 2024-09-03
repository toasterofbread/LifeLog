package dev.toastbits.lifelog.application.worker.mapper

import dev.toastbits.lifelog.application.worker.model.TransferableFileStructure
import dev.toastbits.lifelog.core.filestructure.FileStructure
import dev.toastbits.lifelog.core.filestructure.toSerialisable

actual suspend fun FileStructure.toTransferable(onProgress: (Int) -> Unit): TransferableFileStructure =
   toSerialisable(onProgress)

actual fun TransferableFileStructure.deserialise(): FileStructure = this
