package dev.toastbits.lifelog.extension.test

import dev.toastbits.kotules.runtime.KotuleLoader
import dev.toastbits.lifelog.core.plugin.LifelogPlugin
import dev.toastbits.lifelog.core.plugin.getLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main() {
    GlobalScope.launch {
        val loader: KotuleLoader<LifelogPlugin> = LifelogPlugin::class.getLoader()
        println(1)
        val plugin: LifelogPlugin = loader.loadTestPlugin("dev.toastbits.lifelog.extension.testext.TestExtension")
        println("PLUGIN: $plugin")
    }
}
