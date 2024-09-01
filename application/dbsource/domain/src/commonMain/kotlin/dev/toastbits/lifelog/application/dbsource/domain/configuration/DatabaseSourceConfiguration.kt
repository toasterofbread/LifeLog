package dev.toastbits.lifelog.application.dbsource.domain.configuration

import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType

interface DatabaseSourceConfiguration {
    fun getType(): DatabaseSourceType<*>

    @Composable
    fun getPreviewTitle(): String

    @Composable
    fun getPreviewContent(): String

    @Composable
    fun getInvalidReasonMessages(): Map<Int, String>
}

@Suppress("UNCHECKED_CAST")
fun <C: DatabaseSourceConfiguration> C.castType(): DatabaseSourceType<C> =
    getType() as DatabaseSourceType<C>
