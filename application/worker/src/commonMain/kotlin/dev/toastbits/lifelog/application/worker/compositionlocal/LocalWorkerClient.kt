package dev.toastbits.lifelog.application.worker.compositionlocal

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import dev.toastbits.lifelog.application.worker.WorkerClient

val LocalWorkerClient: ProvidableCompositionLocal<WorkerClient> =
    staticCompositionLocalOf { throw IllegalStateException() }
