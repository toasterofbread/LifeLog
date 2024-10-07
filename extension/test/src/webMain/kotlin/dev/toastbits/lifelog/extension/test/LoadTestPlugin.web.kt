package dev.toastbits.lifelog.extension.test

import dev.toastbits.kotules.runtime.KotuleLoader
import dev.toastbits.lifelog.core.plugin.LifelogPlugin
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

actual suspend fun KotuleLoader<LifelogPlugin>.loadTestPlugin(classPath: String): LifelogPlugin {
    val extensionFileUrl: String = getUrl() + "/testext.js"
    println("Loading extension file at $extensionFileUrl")

    val response: HttpResponse = HttpClient().get(extensionFileUrl)
    check(response.status.isSuccess()) { "${response.status} | ${response.bodyAsText()}" }

    println("Loading SampleKotule in JS code at '$classPath'")

    return loadFromKotlinJsCode(response.bodyAsText(), classPath)
}

internal expect fun getUrl(): String
