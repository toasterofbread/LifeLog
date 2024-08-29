package dev.toastbits.lifelog.application.core

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.composable.ApplicationTheme
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPageWithItems
import dev.toastbits.composekit.settings.ui.ThemeValuesData
import dev.toastbits.composekit.settings.ui.getDefaultCatppuccinThemes
import dev.toastbits.lifelog.application.dbsource.source.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.source.configuration.InMemoryGitDatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.source.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.source.type.InMemoryGitDatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.ui.screen.sourcelist.DatabaseSourceConfigurationList

class Application(
    private val context: PlatformContext,
    private val preferences: PlatformPreferences
) {
    @Composable
    fun Main() {
        val theme: ThemeValuesData = remember {
            getDefaultCatppuccinThemes().first { it.name.lowercase().contains("green") }.theme
        }

        val settingsInterface: SettingsInterface = remember {
            SettingsInterface(
                context,
                preferences,
                { theme },
                0,
                { _, _ ->
                    SettingsPageWithItems({ null }, { emptyList() })
                }
            )
        }

        val sourceTypes: List<DatabaseSourceType<*>> =
            listOf(
                InMemoryGitDatabaseSourceType,
                InMemoryGitDatabaseSourceType,
                InMemoryGitDatabaseSourceType,
                InMemoryGitDatabaseSourceType,
                InMemoryGitDatabaseSourceType,
                InMemoryGitDatabaseSourceType,
                InMemoryGitDatabaseSourceType
            )

        val testSources: List<DatabaseSourceConfiguration> =
            listOf(
                InMemoryGitDatabaseSourceConfiguration("Test", "https://github.com/toasterofbread/test.git"),
                InMemoryGitDatabaseSourceConfiguration("LifeLog", "https://github.com/toasterofbread/lifelog.git"),
                InMemoryGitDatabaseSourceConfiguration("Test (GitLab)", "https://gitlab.com/toasterofbread/test")
            )

        theme.ApplicationTheme(context) {
            Column(Modifier.fillMaxSize()) {
                Text("Hello World! $context")
//                i.Interface(Modifier.fillMaxSize().weight(1f))

                Box(
                    Modifier
                        .padding(20.dp)
                        .border(2.dp, Color.White)
                        .padding(20.dp)
                ) {
                    DatabaseSourceConfigurationList(testSources, sourceTypes) {
                        println("SELECTED $it")
                    }
                }
            }
        }
    }

    fun onClose() {

    }
}
