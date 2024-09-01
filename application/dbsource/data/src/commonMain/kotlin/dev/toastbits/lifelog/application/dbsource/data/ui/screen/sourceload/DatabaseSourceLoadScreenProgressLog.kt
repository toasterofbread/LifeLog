package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourceload

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.composable.wave.WaveLineArea
import dev.toastbits.lifelog.application.dbsource.domain.accessor.DatabaseAccessor
import dev.toastbits.lifelog.application.dbsource.domain.model.LogDatabaseParseResult
import dev.toastbits.lifelog.core.specification.converter.ParseAlertData
import dev.toastbits.lifelog.core.specification.converter.alert.LogConvertAlert
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.`database_loader_finished_$duration_$warnings_$errors`
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

@Composable
internal fun DatabaseSourceLoadScreenProgressLog(
    result: Pair<LogDatabaseParseResult, Duration>?,
    finishedStepsProgress: List<DatabaseAccessor.LoadProgress>,
    currentProgress: DatabaseAccessor.LoadProgress?,
    loadException: Throwable?,
    modifier: Modifier = Modifier
) {
    val theme: ThemeValues = LocalApplicationTheme.current
    val scrollState: LazyListState = rememberLazyListState()

    LaunchedEffect(result) {
        if (result != null) {
            scrollState.scrollToItem(Int.MAX_VALUE)
        }
    }

    val (warnings, errors) = remember(result?.first) {
        result?.first?.alerts?.let { alerts ->
            alerts.filter { it.alert.severity == LogConvertAlert.Severity.WARNING } to alerts.filter { it.alert.severity == LogConvertAlert.Severity.ERROR }
        } ?: Pair(emptyList(), emptyList())
    }

    WaveLineArea(
        modifier,
        periodMillis = 3000,
        playing = result == null
    ) {
        SelectionContainer {
            ScrollBarLazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(10.dp),
                columnModifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(finishedStepsProgress) { loadProgress ->
                    LoadProgressDisplay(loadProgress, done = true)
                }

                currentProgress?.also { progress ->
                    item {
                        LoadProgressDisplay(progress, done = false)
                    }
                }

                loadException?.also { exception ->
                    item {
                        Text(
                            remember(exception) { exception.stackTraceToString() },
                            color = theme.error
                        )
                    }
                }

                result?.also { (parseResult, duration) ->
                    items(parseResult.alerts) { alert ->
                        Text(
                            alert.getText(),
                            style = MaterialTheme.typography.labelLarge,
                            color =
                            when (alert.alert.severity) {
                                LogConvertAlert.Severity.WARNING -> theme.on_background
                                LogConvertAlert.Severity.ERROR -> theme.error
                            }
                        )
                    }

                    item {
                        Text(
                            stringResource(Res.string.`database_loader_finished_$duration_$warnings_$errors`)
                                .replace("\$duration", duration.toString())
                                .replace("\$warnings", warnings.size.toString())
                                .replace("\$errors", errors.size.toString()),
                            Modifier.padding(top = 15.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun ParseAlertData.getText(): String =
    when (alert.severity) {
        LogConvertAlert.Severity.WARNING -> "Warning: "
        LogConvertAlert.Severity.ERROR -> "Error: "
    } + "$alert at $filePath" + lineIndex?.let { ":$it" }.orEmpty()
