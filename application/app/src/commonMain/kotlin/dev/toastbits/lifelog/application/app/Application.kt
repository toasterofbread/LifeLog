package dev.toastbits.lifelog.application.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.navigator.ExtendableNavigator
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.platform.LocalContext
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.composable.theme.ApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValuesData
import dev.toastbits.composekit.settings.ui.getDefaultCatppuccinThemes
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.composekit.utils.common.plus
import dev.toastbits.composekit.utils.common.thenIf
import dev.toastbits.lifelog.application.app.ui.PersistentTopBar
import dev.toastbits.lifelog.application.core.FullContentScreen
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourcelist.DatabaseSourceListScreen
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.DatabaseSourceLoadScreen
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.logview.data.ui.screen.TopLogViewScreen
import dev.toastbits.lifelog.application.settings.data.appsettings.AppSettingsImpl
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.model.SerialisedDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.settings.domain.model.deserialiseConfiguration
import dev.toastbits.lifelog.application.worker.WorkerClient
import dev.toastbits.lifelog.application.worker.compositionlocal.LocalWorkerClient
import dev.toastbits.lifelog.extension.media.GDocsExtension
import dev.toastbits.lifelog.extension.media.MediaExtension
import dev.toastbits.lifelog.extension.mediawatch.MediaWatchExtension

class Application(
    private val context: PlatformContext,
    private val workerClient: WorkerClient,
    preferences: PlatformPreferences,
    private val settings: AppSettings = AppSettingsImpl(preferences)
) {
    private val navigator: Navigator = ExtendableNavigator(initialScreen = DatabaseSourceListScreen())

    init {
        registerExtensions()
    }

    @Composable
    fun Main() {
        LaunchedEffect(Unit) {
            openAutoOpenSource()
        }

        val theme: ThemeValuesData = remember {
            getDefaultCatppuccinThemes().first { it.name.lowercase().contains("green") }.theme
        }

        CompositionLocalProvider(
            LocalContext provides context,
            LocalWorkerClient provides workerClient,
            LocalSettings provides settings,
            LocalNavigator provides navigator,
        ) {
            theme.ApplicationTheme(context) {
                TopContent()
            }
        }
    }

    fun onClose() {

    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return navigator.handleKeyEvent(event)
    }

    private suspend fun openAutoOpenSource() {
        val autoOpenIndex: Int = settings.DatabaseSource.AUTO_OPEN_SOURCE_INDEX.get()
        if (autoOpenIndex < 0) {
            return
        }

        val serialisedSourceConfigurations: List<SerialisedDatabaseSourceConfiguration> = settings.DatabaseSource.DATABASE_SOURCES.get()
        val autoOpenConfiguration: DatabaseSourceConfiguration =
            serialisedSourceConfigurations.getOrNull(autoOpenIndex)?.let {
                settings.DatabaseSource.sourceTypeRegistry.deserialiseConfiguration(it)
            } ?: return

        val loadScreen: Screen =
            DatabaseSourceLoadScreen(
                autoOpenConfiguration,
                onLoaded = {
                    navigator.replaceScreen(TopLogViewScreen(it))
                },
                autoProceed = true
            )

        navigator.pushScreen(loadScreen)
    }

    @Composable
    private fun TopContent() {
        Scaffold { padding ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val fullContentScreen: Boolean = navigator.currentScreen is FullContentScreen

                navigator.CurrentScreen(
                    Modifier.fillMaxHeight().thenIf(!fullContentScreen) { widthIn(max = 1000.dp) },
                    padding + PaddingValues(20.dp)
                ) { modifier, paddingValues, content ->
                    Column(
                        modifier,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        AnimatedVisibility(!fullContentScreen) {
                            PersistentTopBar(Modifier.fillMaxWidth().padding(paddingValues.copy(bottom = 0.dp)))
                        }

                        content(
                            Modifier.fillMaxSize().weight(1f),
                            paddingValues.thenIf(!fullContentScreen) {
                                copy(top = 0.dp)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun registerExtensions() {
        settings.Database.extensionRegistry.registerExtension(MediaExtension())
        settings.Database.extensionRegistry.registerExtension(MediaWatchExtension())
        settings.Database.extensionRegistry.registerExtension(GDocsExtension())
    }
}
