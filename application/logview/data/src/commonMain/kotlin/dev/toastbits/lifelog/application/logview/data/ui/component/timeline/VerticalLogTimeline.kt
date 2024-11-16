package dev.toastbits.lifelog.application.logview.data.ui.component.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastLastOrNull
import androidx.compose.ui.zIndex
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.common.thenIf
import dev.toastbits.composekit.utils.common.thenWith
import dev.toastbits.composekit.utils.composable.stickyHeaderContentPaddingAware
import dev.toastbits.lifelog.application.logview.data.ui.component.timeline.item.DateTimelineItem
import dev.toastbits.lifelog.application.logview.data.ui.component.timeline.item.EventTimelineItem
import dev.toastbits.lifelog.application.logview.data.ui.component.timeline.item.TimelineItem
import dev.toastbits.lifelog.application.logview.data.ui.component.timeline.item.rememberTimelineItems
import dev.toastbits.lifelog.application.logview.data.ui.screen.LogEventReference
import dev.toastbits.lifelog.core.specification.database.LogDatabase
import dev.toastbits.lifelog.core.specification.model.entity.date.LogDate
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.sin

private const val WAVE_AMPLITUDE: Float = 9f
private const val WAVE_THICKNESS: Float = 1.5f

@Composable
internal fun VerticalLogTimeline(
    logDatabase: LogDatabase,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollTargetDateIndex: Int? = null,
    onCurrentDateIndexChanged: ((Int) -> Unit)? = null,
    onEventSelected: ((LogEventReference) -> Unit)? = null
) {
    val theme: ThemeValues = LocalApplicationTheme.current
    val density: Density = LocalDensity.current

    val timelineItems: List<TimelineItem> = logDatabase.rememberTimelineItems()
    val columnState: LazyListState = rememberLazyListState()

    val currentDateIndex: Int? = columnState.layoutInfo.visibleItemsInfo.firstNotNullOfOrNull { it.key as? Int }
    LaunchedEffect(currentDateIndex) {
        if (onCurrentDateIndexChanged == null || currentDateIndex == null) {
            return@LaunchedEffect
        }

        onCurrentDateIndexChanged(currentDateIndex)
    }

    LaunchedEffect(scrollTargetDateIndex) {
        if (scrollTargetDateIndex == null) {
            return@LaunchedEffect
        }

        val dateIndex: Int =
            if (scrollTargetDateIndex == Int.MAX_VALUE)
                (timelineItems.lastOrNull { it is DateTimelineItem } as DateTimelineItem?)?.index ?: return@LaunchedEffect
            else if (scrollTargetDateIndex == Int.MIN_VALUE) 0
            else scrollTargetDateIndex

        val scrollIndex: Int =
            timelineItems.indexOfFirst { item ->
                (item as? DateTimelineItem)?.index == dateIndex
            }

        if (scrollIndex == -1) {
            return@LaunchedEffect
        }

        onCurrentDateIndexChanged?.invoke(dateIndex)

        columnState.animateScrollToItem(
            index = scrollIndex,
            scrollOffset = with (density) { contentPadding.calculateTopPadding().roundToPx() + 5 }
        )
    }

    ScrollBarLazyColumn(
        modifier,
        state = columnState,
        contentPadding = contentPadding
    ) {
        for (item in timelineItems) {
            when (item) {
                is DateTimelineItem ->
                    stickyHeaderContentPaddingAware(columnState, key = item.index) {
                        Item(item, onEventSelected, density, theme)
                    }
                is EventTimelineItem ->
                    item(key = item.event.hashCode().toString()) {
                        Item(item, onEventSelected, density, theme)
                    }
            }
        }
    }
}

@Composable
private fun Item(
    item: TimelineItem,
    onEventSelected: ((LogEventReference) -> Unit)?,
    density: Density,
    theme: ThemeValues
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .thenWith(
                item as? EventTimelineItem,
                onEventSelected
            ) { event, callback ->
                clip(RoundedCornerShape(10.dp))
                    .clickable {
                        callback(event.event)
                    }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        var mainContentHeight: Dp by remember { mutableStateOf(0.dp) }
        var iconContentHeight: Dp by remember { mutableStateOf(0.dp) }

        Row(
            Modifier.fillMaxWidth(0.4f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val metadataColumnFill: Float = 0.7f

            if (!item.hasWideIcon) {
                Column(Modifier.fillMaxWidth(metadataColumnFill)) {
                    item.MetadataItems(Modifier)
                }
            }

            Box(
                Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                item.IconContent(
                    Modifier
                        .onSizeChanged {
                            iconContentHeight = with(density) { it.height.toDp() }
                        }
                        .zIndex(1f)
                        .thenIf(!item.hasWideIcon) {
                            offset(y = (iconContentHeight - mainContentHeight) / 2f)
                        }
                )

                Row {
                    if (item.hasWideIcon) {
                        Box(Modifier.fillMaxWidth(metadataColumnFill))
                    }
                    TimelineItemWave(theme.accent, Modifier.fillMaxSize())
                }
            }
        }

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleSmall,
            LocalContentColor provides LocalContentColor.current.copy(alpha = 0.7f)
        ) {
            item.MainContent(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
                    .onSizeChanged {
                        mainContentHeight = with(density) { it.height.toDp() }
                    }
            )
        }
    }
}

@Composable
private fun TimelineItemWave(colour: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.clipToBounds()) {
        translate(left = size.width / 2f) {
            rotate(90f, pivot = Offset.Zero) {
                val path: Path = Path()
                for (direction in listOf(-1, 1)) {
                    wavePath(path, direction, WAVE_AMPLITUDE, 20.dp, 0f, 0f)
                    drawPath(path, colour, style = Stroke(WAVE_THICKNESS))
                }
            }
        }
    }
}

private fun DrawScope.wavePath(
    path: Path,
    direction: Int,
    height: Float,
    wavelength: Dp,
    outerRotationDegrees: Float = 0f,
    offset: Float
): Path {
    path.reset()

    val halfPeriod: Float = wavelength.toPx() / 2

    val rotationAdj: Float = sin(outerRotationDegrees.toRadians())
    val maxSize: Float = maxOf(size.width, size.height)

    val effectiveWidth: Float = ceil(maxSize / halfPeriod) * halfPeriod

    val yOffset: Float = -(maxSize * rotationAdj * 0.5f)

    check(offset in 0f .. 1f) { offset }

    val xOffset: Float = offset * halfPeriod * 2
    val xAdjustedOffset = (xOffset % effectiveWidth) - (if (xOffset > 0f) effectiveWidth else 0f)
    path.moveTo(x = -halfPeriod / 2 + xAdjustedOffset, y = yOffset)

    for (i in 0 until ceil((effectiveWidth * 2) / halfPeriod + 1).toInt()) {
        if ((i % 2 == 0) != (direction == 1)) {
            path.relativeMoveTo(halfPeriod, 0f)
            continue
        }

        path.relativeQuadraticTo(
            dx1 = halfPeriod / 2,
            dy1 = height / 2 * direction,
            dx2 = halfPeriod,
            dy2 = 0f
        )
    }

    return path
}

private fun Float.toRadians(): Float =
    (this * 180f) / PI.toFloat()

