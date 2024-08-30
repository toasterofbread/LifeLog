package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload

import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.platform.composable.theme.ThemedLinearProgressIndicator
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.composable.NullableValueAnimatedVisibility
import dev.toastbits.lifelog.application.dbsource.data.ui.component.DatabaseSourceConfigurationPreview
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step.LoadStepCheckIfUpToDate
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step.LoadStepLoadOnline
import dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload.step.LoadStep
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.accessor.OfflineDatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.configuration.castType
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import dev.toastbits.lifelog.application.navigation.navigator.Navigator
import dev.toastbits.lifelog.application.navigation.Screen
import dev.toastbits.lifelog.application.settings.data.compositionlocal.LocalSettings
import dev.toastbits.lifelog.application.settings.domain.appsettings.AppSettings
import dev.toastbits.lifelog.application.settings.domain.group.toCurrentLogDatabaseConfiguration
import dev.toastbits.lifelog.core.accessor.LogDatabaseConfiguration
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.database_loader_title
import org.jetbrains.compose.resources.stringResource

class DatabaseSourceLoadScreen(
    private val sourceConfiguration: DatabaseSourceConfiguration
): Screen {
    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier) {
        val settings: AppSettings = LocalSettings.current
        var logDatabaseConfiguration: LogDatabaseConfiguration? by remember { mutableStateOf(null) }

        // TEMP
        val client = remember { HttpClient() }
        val ioDispatcher = Dispatchers.Default

        LaunchedEffect(Unit) {
            logDatabaseConfiguration = settings.Database.toCurrentLogDatabaseConfiguration()
        }

        logDatabaseConfiguration?.also { databaseConfiguration ->
            val databaseAccessor: DatabaseAccessor =
                remember(databaseConfiguration) {
                    sourceConfiguration.castType().createAccessor(
                        sourceConfiguration,
                        databaseConfiguration,
                        client,
                        ioDispatcher
                    )
                }

            DatabaseSourceLoader(
                sourceConfiguration,
                databaseAccessor,
                modifier,
                onLoaded = {
                    println("DATABASE LOADED $it")
                }
            )
        }
    }
}

@Composable
internal fun DatabaseSourceLoader(
    sourceConfiguration: DatabaseSourceConfiguration,
    databaseAccessor: DatabaseAccessor,
    modifier: Modifier = Modifier,
    onLoaded: (LogDatabaseParseResult) -> Unit
) {
    var loadException: Throwable? by remember { mutableStateOf(null) }
    var currentStep: LoadStep =
        remember(databaseAccessor) {
            if (databaseAccessor is OfflineDatabaseAccessor) LoadStepCheckIfUpToDate(databaseAccessor)
            else LoadStepLoadOnline
        }
    var progress: DatabaseAccessor.LoadProgress? by remember { mutableStateOf(null) }

    LaunchedEffect(currentStep) {
        val result: LoadStep.ExecuteResult = currentStep.execute(databaseAccessor) { progress = it }
        when (result) {
            is LoadStep.ExecuteResult.DatabaseLoaded -> onLoaded(result.parseResult)
            is LoadStep.ExecuteResult.ExceptionThrown -> {
                result.exception.printStackTrace()
                loadException = result.exception
            }
            is LoadStep.ExecuteResult.NextStep -> currentStep = result.nextStep
        }
    }

    Column(modifier) {
        Text(stringResource(Res.string.database_loader_title))
        Text(currentStep.getStatusText(databaseAccessor))

        val loadProgress: DatabaseAccessor.LoadProgress? = progress
        if (loadProgress != null) {
            LoadProgressDisplay(loadProgress)
        }

        loadException?.also {
            Text("EXCEPTION: $loadException")
        }

        DatabaseSourceConfigurationPreview(sourceConfiguration)
    }
}

@Composable
private fun LoadProgressDisplay(progress: DatabaseAccessor.LoadProgress, modifier: Modifier = Modifier) {
    val theme: ThemeValues = LocalApplicationTheme.current

    Column(modifier) {
        Text(progress.getMessage())

        Row(verticalAlignment = Alignment.CenterVertically) {
            NullableValueAnimatedVisibility(
                progress.getProgressMessage(),
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) { message ->
                if (message != null) {
                    Text(message)
                }
            }

            if (progress is DatabaseAccessor.LoadProgress.Absolute) {
                theme.ThemedLinearProgressIndicator({ progress.progressFraction })
            }
            else {
                theme.ThemedLinearProgressIndicator()
            }
        }
    }
}
