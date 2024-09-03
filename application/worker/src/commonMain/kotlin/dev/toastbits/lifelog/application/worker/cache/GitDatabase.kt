package dev.toastbits.lifelog.application.worker.cache

import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.lifelog.application.worker.GitDatabase

internal expect suspend fun GitDatabase.Companion.createInstance(context: PlatformContext): GitDatabase
