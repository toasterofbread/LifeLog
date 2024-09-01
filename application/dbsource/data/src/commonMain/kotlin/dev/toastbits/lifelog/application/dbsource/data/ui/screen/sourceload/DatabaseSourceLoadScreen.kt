package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.configuration.castType
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.group.getGitCredentials
import dev.toastbits.lifelog.application.settings.domain.group.rememberCurrentLogDatabaseConfiguration
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.database_loader_title
import org.jetbrains.compose.resources.stringResource

class DatabaseSourceLoadScreen(
    private val sourceConfiguration: DatabaseSourceConfiguration,
    private val onLoaded: (LogDatabase) -> Unit
): Screen {
    override val title: String?
        @Composable get() = stringResource(Res.string.database_loader_title)

    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        // TEMP
        val client = remember { HttpClient() }
        val ioDispatcher = Dispatchers.Default
        val workDispatcher = Dispatchers.Default

        val settings: AppSettings = LocalSettings.current

        val logDatabaseConfiguration: LogDatabaseConfiguration = settings.Database.rememberCurrentLogDatabaseConfiguration()
        val databaseAccessor: DatabaseAccessor =
            remember(logDatabaseConfiguration) {
                sourceConfiguration.castType().createAccessor(
                    sourceConfiguration,
                    logDatabaseConfiguration,
                    settings.DatabaseSource::getGitCredentials,
                    client,
                    ioDispatcher,
                    workDispatcher
                )
            }

        DatabaseSourceLoader(
            sourceConfiguration,
            databaseAccessor,
            modifier.padding(contentPadding),
            onProceeded = onLoaded
        )
    }
}
