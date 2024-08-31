package dev.toastbits.lifelog.application.dbsource.domain.type

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.configuration.castType
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import dev.toastbits.lifelog.core.git.model.GitCredentials
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher

interface DatabaseSourceType<C: DatabaseSourceConfiguration> {
    fun isAvailableOnPlatform(): Boolean

    fun createNewConfiguration(): C

    fun createAccessor(
        configuration: C,
        databaseConfiguration: LogDatabaseConfiguration,
        gitCredentials: GitCredentials?,
        httpClient: HttpClient,
        ioDispatcher: CoroutineDispatcher,
        workDispatcher: CoroutineDispatcher
    ): DatabaseAccessor

    fun serialiseConfiguration(configuration: C): String
    fun deserialiseConfiguration(serialisedConfiguration: String): C

    @Composable
    fun getName(): String

    @Composable
    fun getDescription(): String

    @Composable
    fun getIcon(): ImageVector

    fun LazyListScope.lazyListConfigurationItems(configuration: C, modifier: Modifier = Modifier, onChange: (C) -> Unit)
}

fun <C: DatabaseSourceConfiguration> C.lazyListConfigurationItems(scope: LazyListScope, modifier: Modifier = Modifier, onChange: (C) -> Unit) {
    val type: DatabaseSourceType<C> = castType()
    with (type) {
        scope.lazyListConfigurationItems(this@lazyListConfigurationItems, modifier, onChange)
    }
}
