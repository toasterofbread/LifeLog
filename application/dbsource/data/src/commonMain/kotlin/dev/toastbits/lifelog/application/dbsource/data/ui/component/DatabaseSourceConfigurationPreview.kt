package dev.toastbits.lifelog.application.dbsource.data.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.common.thenWith
import dev.toastbits.lifelog.application.dbsource.domain.configuration.DatabaseSourceConfiguration
import dev.toastbits.lifelog.application.dbsource.domain.type.DatabaseSourceType
import lifelog.application.dbsource.data.generated.resources.Res
import lifelog.application.dbsource.data.generated.resources.database_source_is_set_to_auto_open
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabaseSourceConfigurationPreview(
    configuration: DatabaseSourceConfiguration,
    modifier: Modifier = Modifier,
    autoOpens: Boolean = false,
    onSelected: (() -> Unit)? = null
) {
    val type: DatabaseSourceType<*> = configuration.getType()
    val theme: ThemeValues = LocalApplicationTheme.current
    val shape: CornerBasedShape = MaterialTheme.shapes.medium

    Row(
        modifier
            .clip(shape)
            .thenWith(onSelected) {
                clickable(onClick = it)
            }
            .border(2.dp, theme.accent, shape)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(type.getIcon(), type.getName())

        Column(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                configuration.getPreviewTitle(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                configuration.getPreviewContent(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.7f)
            )
        }

        AnimatedVisibility(autoOpens) {
            Icon(Icons.Default.Flag, stringResource(Res.string.database_source_is_set_to_auto_open))
        }
    }
}