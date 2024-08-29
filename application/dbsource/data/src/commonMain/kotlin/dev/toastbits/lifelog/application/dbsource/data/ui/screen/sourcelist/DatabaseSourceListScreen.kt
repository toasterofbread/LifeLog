package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourcelist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceconfiguration.DatabaseSourceConfigurationScreen
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.DatabaseSourceLoadScreen
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.deserialiseConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.serialiseConfiguration

class DatabaseSourceListScreen: Screen {
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        val settings: AppSettings = LocalSettings.current
        val autoOpenIndex: Int? by settings.DatabaseSource.AUTO_OPEN_SOURCE_INDEX.observe()

        var serialisedSourceConfigurations: List<SerialisedDatabaseSourceConfiguration> by settings.DatabaseSource.DATABASE_SOURCES.observe()
        val sourceConfigurations: List<DatabaseSourceConfiguration> = remember(serialisedSourceConfigurations) {
            serialisedSourceConfigurations.map { serialised ->
                settings.DatabaseSource.sourceTypeRegistry.deserialiseConfiguration(serialised)
            }
        }

        val sourceTypes: List<DatabaseSourceType<*>> = settings.DatabaseSource.sourceTypeRegistry.getAll().values.toList()

        DatabaseSourceList(
            sourceConfigurations,
            sourceTypes,
            Modifier.fillMaxSize(),
            autoOpenConfigurationIndex = autoOpenIndex,
            onSelected = { index ->
                val source: DatabaseSourceConfiguration = sourceConfigurations[index]
                navigator.push(DatabaseSourceLoadScreen(source))
            },
            onTypeAddRequested = { index ->
                val newConfigurationIndex: Int = sourceConfigurations.size

                val type: DatabaseSourceType<*> = sourceTypes[index]
                serialisedSourceConfigurations += settings.DatabaseSource.sourceTypeRegistry.serialiseConfiguration(type.createNewConfiguration())

                navigator.push(DatabaseSourceConfigurationScreen(newConfigurationIndex))
            }
        )
    }
}