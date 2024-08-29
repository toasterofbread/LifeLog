package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceconfiguration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.type.lazyListConfigurationItems
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.deserialiseConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.serialiseConfiguration

internal class DatabaseSourceConfigurationScreen(
    private val sourceIndex: Int
): Screen {
    @Composable
    override fun Content() {
        val settings: AppSettings = LocalSettings.current
        var sources: List<SerialisedDatabaseSourceConfiguration> by settings.DatabaseSource.DATABASE_SOURCES.observe()

        val serialisedSource: SerialisedDatabaseSourceConfiguration? = sources.getOrNull(sourceIndex)
        val source: DatabaseSourceConfiguration? = remember(serialisedSource) {
            serialisedSource?.let { settings.DatabaseSource.sourceTypeRegistry.deserialiseConfiguration(it) }
        }

        println("SOURCE $source $sourceIndex $sources")

        ScrollBarLazyColumn {
            source?.lazyListConfigurationItems(this) { newConfiguration ->
                val serialised: SerialisedDatabaseSourceConfiguration = settings.DatabaseSource.sourceTypeRegistry.serialiseConfiguration(newConfiguration)
                sources = sources.toMutableList().apply { set(sourceIndex, serialised) }
            }
        }
    }
}
