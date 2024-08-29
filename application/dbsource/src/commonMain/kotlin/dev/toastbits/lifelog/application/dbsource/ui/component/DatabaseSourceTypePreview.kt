package dev.toastbits.lifelog.application.dbsource.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.utils.common.thenWith
import dev.toastbits.lifelog.application.dbsource.source.type.DatabaseSourceType
import lifelog.application.dbsource.generated.resources.Res
import lifelog.application.dbsource.generated.resources.database_source_is_set_to_auto_open
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabaseSourceTypePreview(
    sourceType: DatabaseSourceType<*>,
    modifier: Modifier = Modifier,
    autoOpens: Boolean = false,
    onSelect: (() -> Unit)? = null
) {
    val shape: CornerBasedShape = MaterialTheme.shapes.small

    Row(
        modifier
            .width(IntrinsicSize.Max)
            .clip(shape)
            .thenWith(onSelect) {
                clickable(onClick = it)
            }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(sourceType.getIcon(), sourceType.getName())

        Column(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                sourceType.getName(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                sourceType.getDescription(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.7f)
            )
        }

        AnimatedVisibility(autoOpens) {
            Icon(Icons.Default.Flag, stringResource(Res.string.database_source_is_set_to_auto_open))
        }
    }
}