package dev.toastbits.lifelog.application.logview.data.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.lifelog.application.core.FullContentScreen
import dev.toastbits.lifelog.application.logview.data.ui.component.timeline.VerticalLogTimeline
import dev.toastbits.lifelog.core.specification.database.LogDatabase

class TopLogViewScreen(
    private val logDatabase: LogDatabase
): Screen, FullContentScreen {
    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        val innerPadding: Dp = 20.dp

        Row(modifier) {
            VerticalLogTimeline(
                logDatabase,
                Modifier.fillMaxWidth(0.3f).fillMaxHeight(),
                contentPadding = contentPadding.copy(end = innerPadding)
            )
            Box(Modifier.fillMaxSize().weight(1f).background(Color.DarkGray))
        }
    }
}
