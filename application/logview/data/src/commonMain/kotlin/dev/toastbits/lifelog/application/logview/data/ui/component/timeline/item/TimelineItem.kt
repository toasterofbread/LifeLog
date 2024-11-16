package dev.toastbits.lifelog.application.logview.data.ui.component.timeline.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.toastbits.lifelog.application.logview.data.ui.screen.LogEventReference
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import dev.toastbits.lifelog.core.specification.model.entity.event.LogEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal sealed interface TimelineItem {
    @Composable
    fun MainContent(modifier: Modifier)

    @Composable
    fun MetadataItems(itemModifier: Modifier)

    @Composable
    fun IconContent(modifier: Modifier)

    val hasWideIcon: Boolean
        get() = false
}

@Composable
internal fun LogDatabase.rememberTimelineItems(): List<TimelineItem> {
    var items: List<TimelineItem> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(this) {
        withContext(Dispatchers.Default) {
            val sortedDays: List<Map.Entry<LogDate, List<LogEvent>>> =
                this@rememberTimelineItems.days.entries.sortedBy { it.key.date }

            items = buildList {
                var dateIndex: Int = 0
                for ((date, events) in sortedDays) {
                    if (events.isEmpty()) {
                        continue
                    }

                    add(DateTimelineItem(date, dateIndex++))
                    for (index in events.indices) {
                        add(EventTimelineItem(LogEventReference(date, index), this@rememberTimelineItems))
                    }
                }
            }
        }
    }

    return items
}
