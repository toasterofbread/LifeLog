package dev.toastbits.lifelog.application.logview.data.ui.component.timeline.item

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.lifelog.application.logview.data.ui.toImageVector
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.database.findTypeOfEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEventType

data class EventTimelineItem(val event: LogEvent, val logDatabase: LogDatabase): TimelineItem {
    @Composable
    override fun MainContent(modifier: Modifier) {

    }

    @Composable
    override fun MetadataItems(itemModifier: Modifier) {

    }

    @Composable
    override fun IconContent(modifier: Modifier) {
//        val eventType: LogEventType = logDatabase.configuration.findTypeOfEvent(event)
        Icon(event.getIcon().toImageVector(), null, modifier)
    }
}
