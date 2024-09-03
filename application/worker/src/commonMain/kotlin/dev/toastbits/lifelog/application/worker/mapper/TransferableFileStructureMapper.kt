package dev.toastbits.lifelog.application.worker.mapper

import dev.toastbits.lifelog.application.worker.model.TransferableFileStructure
import dev.toastbits.lifelog.core.filestructure.FileStructure

expect suspend fun FileStructure.toTransferable(onProgress: (Int) -> Unit = {}): TransferableFileStructure

expect fun TransferableFileStructure.deserialise(): FileStructure
