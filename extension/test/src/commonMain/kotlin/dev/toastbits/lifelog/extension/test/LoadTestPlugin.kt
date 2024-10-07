package dev.toastbits.lifelog.extension.test

import dev.toastbits.kotules.runtime.KotuleLoader
import dev.toastbits.lifelog.core.plugin.LifelogPlugin

expect suspend fun KotuleLoader<LifelogPlugin>.loadTestPlugin(classPath: String): LifelogPlugin
