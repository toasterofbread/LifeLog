package dev.toastbits.lifelog.application.dbsource.data.ui.screen.sourcelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import dev.toastbits.lifelog.application.dbsource.data.ui.component.DatabaseSourceConfigurationPreview
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.database_source_list_no_sources_added
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DatabaseSourceList(
    configurations: List<DatabaseSourceConfiguration>,
    types: List<DatabaseSourceType<*>>,
    modifier: Modifier = Modifier,
    autoOpenConfigurationIndex: Int? = null,
    onSelected: ((Int) -> Unit)? = null,
    onTypeAddRequested: ((Int) -> Unit)? = null
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        if (types.isNotEmpty()) {
            item {
                DatabaseSourceListActions(
                    types,
                    onTypeSelected = onTypeAddRequested,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (configurations.isEmpty()) {
            item {
                Text(stringResource(Res.string.database_source_list_no_sources_added))
            }
        } else {
            itemsIndexed(configurations) { index, sourceConfiguration ->
                DatabaseSourceConfigurationPreview(
                    sourceConfiguration,
                    autoOpens = index == autoOpenConfigurationIndex,
                    onSelected = onSelected?.let { { it(index) } }
                )
            }
        }
    }
}