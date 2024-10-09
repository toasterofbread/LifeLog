package dev.toastbits.lifelog.extension.test

import dev.toastbits.kotules.runtime.KotuleLoader
import dev.toastbits.lifelog.core.plugin.LifelogPlugin
import dev.toastbits.lifelog.core.plugin.getLoader
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main() {
    GlobalScope.launch {
        val loader: KotuleLoader<LifelogPlugin> = LifelogPlugin::class.getLoader()
        val plugin: LifelogPlugin = loader.loadTestPlugin("dev.toastbits.lifelog.extension.testext.TestExtension")
        println("PLUGIN: $plugin")
        println("ID: ${plugin.id.length}")

        val extraEventTypes: List<LogEventType> = plugin.extraEventTypes
        println("extraEventTypes $extraEventTypes")

        for (type in extraEventTypes) {
            println("TYPE $type")

            println("TYPE.NAME ${type.name}")
        }
    }
}
