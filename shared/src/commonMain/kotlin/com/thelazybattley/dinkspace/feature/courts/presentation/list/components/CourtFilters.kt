package com.thelazybattley.dinkspace.feature.courts.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHour
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHourRange
import com.thelazybattley.dinkspace.feature.courts.presentation.list.CourtFilterMode
import com.thelazybattley.dinkspace.feature.courts.presentation.list.TimeRangeSelection
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.courts_filter_date_time
import dinkspace.shared.generated.resources.courts_filter_name
import dinkspace.shared.generated.resources.courts_time_hint_end
import dinkspace.shared.generated.resources.courts_time_hint_start
import dinkspace.shared.generated.resources.date_today
import dinkspace.shared.generated.resources.weekday_fri
import dinkspace.shared.generated.resources.weekday_mon
import dinkspace.shared.generated.resources.weekday_sat
import dinkspace.shared.generated.resources.weekday_sun
import dinkspace.shared.generated.resources.weekday_thu
import dinkspace.shared.generated.resources.weekday_tue
import dinkspace.shared.generated.resources.weekday_wed
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilterModeTabs(
    selected: CourtFilterMode,
    onSelect: (CourtFilterMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = CourtFilterMode.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                icon = {},
            ) {
                Text(
                    stringResource(
                        when (mode) {
                            CourtFilterMode.DATE_TIME -> Res.string.courts_filter_date_time
                            CourtFilterMode.NAME -> Res.string.courts_filter_name
                        },
                    ),
                )
            }
        }
    }
}

@Composable
fun DateChips(
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(dates) { index, date ->
            val isSelected = date == selectedDate
            val weekday = if (index == 0) stringResource(Res.string.date_today) else stringResource(date.dayOfWeek.shortName())
            SelectableTile(
                selected = isSelected,
                onClick = { onSelect(date) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(width = 60.dp, height = 64.dp),
            ) { contentColor, secondaryColor ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = weekday, style = MaterialTheme.typography.labelMedium, color = secondaryColor)
                    Text(text = date.day.toString(), style = MaterialTheme.typography.titleMedium, color = contentColor)
                }
            }
        }
    }
}

@Composable
fun TimeRangePicker(
    selection: TimeRangeSelection,
    onHourClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hours = TimeRangeSelection.selectableHours.toList()
    // Open with the selected start in view, one chip of context to its left.
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selection.range.startHour - TimeRangeSelection.FIRST_HOUR - 1).coerceAtLeast(0),
    )
    val rangeLabel = formatHourRange(selection.range.startHour, selection.range.endHour)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = rangeLabel, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(
                    if (selection.isPickingEnd) Res.string.courts_time_hint_end else Res.string.courts_time_hint_start,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(hours) { hour ->
                HourChip(
                    label = formatHour(hour),
                    isEdge = selection.isEdge(hour),
                    isBetween = selection.isBetween(hour),
                    onClick = { onHourClick(hour) },
                )
            }
        }
    }
}

@Composable
private fun HourChip(
    label: String,
    isEdge: Boolean,
    isBetween: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val container = when {
        isEdge -> colors.primary
        isBetween -> colors.primaryContainer
        else -> colors.surface
    }
    val content = when {
        isEdge -> colors.onPrimary
        isBetween -> colors.onPrimaryContainer
        else -> colors.onSurface
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = container,
        contentColor = content,
        border = if (isEdge || isBetween) null else BorderStroke(1.dp, colors.outline),
        modifier = Modifier
            .defaultMinSize(minWidth = 64.dp)
            .height(44.dp)
            .semantics {
                role = Role.Button
                selected = isEdge || isBetween
                contentDescription = label
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SelectableTile(
    selected: Boolean,
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    content: @Composable (contentColor: Color, secondaryColor: Color) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = shape,
        color = if (selected) colors.primary else colors.surface,
        border = if (selected) null else BorderStroke(1.dp, colors.outline),
        modifier = modifier.semantics { this.selected = selected },
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            content(
                if (selected) colors.onPrimary else colors.onSurface,
                if (selected) colors.onPrimary.copy(alpha = 0.8f) else colors.onSurfaceVariant,
            )
        }
    }
}

private fun DayOfWeek.shortName(): StringResource = when (this) {
    DayOfWeek.MONDAY -> Res.string.weekday_mon
    DayOfWeek.TUESDAY -> Res.string.weekday_tue
    DayOfWeek.WEDNESDAY -> Res.string.weekday_wed
    DayOfWeek.THURSDAY -> Res.string.weekday_thu
    DayOfWeek.FRIDAY -> Res.string.weekday_fri
    DayOfWeek.SATURDAY -> Res.string.weekday_sat
    DayOfWeek.SUNDAY -> Res.string.weekday_sun
}
