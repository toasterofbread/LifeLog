package dev.toastbits.lifelog.application.dbsource.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import dev.toastbits.lifelog.application.dbsource.source.type.DatabaseSourceType

@Composable
fun DatabaseSourceTypeSelector(sourceTypes: List<DatabaseSourceType<*>>, onTypeSelected: (Int) -> Unit) {
    LazyColumn {
        itemsIndexed(sourceTypes) { index, sourceType ->
            DatabaseSourceTypePreview(
                sourceType,
                onSelect = {
                    onTypeSelected(index)
                }
            )
        }
    }
}
