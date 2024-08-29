package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourcelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import dev.toastbits.composekit.platform.composable.ScrollBarLazyRow
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.composable.wave.WaveLineArea
import dev.toastbits.lifelog.application.dbsource.data.ui.component.DatabaseSourceTypePreview
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.database_source_list_add_source
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DatabaseSourceListActions(
    types: List<DatabaseSourceType<*>>,
    modifier: Modifier = Modifier,
    onTypeSelected: ((Int) -> Unit)? = null
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
                Text(
                    stringResource(Res.string.database_source_list_add_source),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            ScrollBarLazyRow(
                Modifier.fillMaxWidth().weight(1f, true),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                scrollbarSpacing = 10.dp
            ) {
                itemsIndexed(types) { index, type ->
                    DatabaseSourceTypePreview(
                        type,
                        Modifier.fillMaxWidth(),
                        onSelect = onTypeSelected?.let {{ it(index) }}
                    )
                }
            }
        }
    }
}