package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceconfiguration

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.type.lazyListConfigurationItems
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.deserialiseConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.serialiseConfiguration

internal class DatabaseSourceConfigurationScreen(
    private val sourceIndex: Int
): Screen {
    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        val settings: AppSettings = LocalSettings.current
        var sources: List<SerialisedDatabaseSourceConfiguration> by settings.DatabaseSource.DATABASE_SOURCES.observe()

        val serialisedSource: SerialisedDatabaseSourceConfiguration? = sources.getOrNull(sourceIndex)
        val source: DatabaseSourceConfiguration? = remember(serialisedSource) {
            serialisedSource?.let { settings.DatabaseSource.sourceTypeRegistry.deserialiseConfiguration(it) }
        }

        ScrollBarLazyColumn(modifier, contentPadding = contentPadding) {
            source?.lazyListConfigurationItems(this) { newConfiguration ->
                val serialised: SerialisedDatabaseSourceConfiguration = settings.DatabaseSource.sourceTypeRegistry.serialiseConfiguration(newConfiguration)
                sources = sources.toMutableList().apply { set(sourceIndex, serialised) }
            }
        }
    }
}
