package dev.toastbits.lifelog.application.logview.data.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.navigation.screen.ResponsiveTwoPaneScreen
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.lifelog.application.core.FullContentScreen
import dev.toastbits.lifelog.application.logview.data.ui.component.timeline.VerticalLogTimeline
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate

data class LogReference(val date: LogDate, val logIndex: Int)

class TopLogViewScreen(
    private val logDatabase: LogDatabase
): ResponsiveTwoPaneScreen<LogReference>(), FullContentScreen {
//    @Composable
//    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
//        val innerPadding: Dp = 20.dp
//
//        Row(modifier) {
//            VerticalLogTimeline(
//                logDatabase,
//                Modifier.fillMaxWidth(0.3f).fillMaxHeight(),
//                contentPadding = contentPadding.copy(end = innerPadding)
//            )
//            Box(Modifier.fillMaxSize().weight(1f).background(Color.DarkGray))
//        }
//    }

    @Composable
    override fun getCurrentData(): LogReference? = null

    @Composable
    override fun PrimaryPane(data: LogReference?, contentPadding: PaddingValues, modifier: Modifier) {
        Text("Primary")
    }

    @Composable
    override fun SecondaryPane(data: LogReference?, contentPadding: PaddingValues, modifier: Modifier) {
        Text("Secondary")
    }
}
