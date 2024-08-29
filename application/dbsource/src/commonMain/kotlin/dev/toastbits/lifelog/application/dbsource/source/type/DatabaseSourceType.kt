package dev.toastbits.lifelog.application.dbsource.source.type

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.lifelog.application.dbsource.source.configuration.DatabaseSourceConfiguration

interface DatabaseSourceType<C: DatabaseSourceConfiguration> {
    fun isAvailableOnPlatform(): Boolean

    @Composable
    fun getName(): String

    @Composable
    fun getDescription(): String

    @Composable
    fun getIcon(): ImageVector

    @Composable
    fun ColumnScope.ConfigurationItems(modifier: Modifier, configuration: C, onChange: (C) -> Unit)
}
