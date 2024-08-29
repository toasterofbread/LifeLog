package dev.toastbits.lifelog.application.dbsource.ui.screen.sourcelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.composable.LocalApplicationTheme
import dev.toastbits.composekit.platform.composable.ScrollBarLazyRow
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.composable.wave.WaveLineArea
import dev.toastbits.lifelog.application.dbsource.source.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.source.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.ui.component.DatabaseSourceConfigurationPreview
import dev.toastbits.lifelog.application.dbsource.ui.component.DatabaseSourceTypePreview
import lifelog.application.dbsource.generated.resources.Res
import lifelog.application.dbsource.generated.resources.database_source_list_add_source
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabaseSourceConfigurationList(
    configurations: List<DatabaseSourceConfiguration>,
    types: List<DatabaseSourceType<*>>,
    modifier: Modifier = Modifier,
    onSelected: ((Int) -> Unit)? = null
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        if (types.isNotEmpty()) {
            item {
                DatabaseSourceConfigurationListActions(
                    types,
                    onTypeSelected = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        itemsIndexed(configurations) { index, sourceConfiguration ->
            DatabaseSourceConfigurationPreview(sourceConfiguration, onSelected = onSelected?.let { { it(index) }})
        }
    }
}

@Composable
fun DatabaseSourceConfigurationListActions(
    types: List<DatabaseSourceType<*>>,
    onTypeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme: ThemeValues = LocalApplicationTheme.current
    WaveLineArea(
        theme.accent.copy(alpha = 0.25f),
        modifier.height(100.dp),
        periodMillis = 5000
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Text(stringResource(Res.string.database_source_list_add_source), style = MaterialTheme.typography.titleSmall)
            }

            ScrollBarLazyRow(
                Modifier.fillMaxWidth().weight(1f, true),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                scrollbarSpacing = 10.dp
            ) {
                itemsIndexed(types) { index, type ->
                    DatabaseSourceTypePreview(type, Modifier.fillMaxWidth()) { onTypeSelected(index) }
                }
            }
        }
    }
}
