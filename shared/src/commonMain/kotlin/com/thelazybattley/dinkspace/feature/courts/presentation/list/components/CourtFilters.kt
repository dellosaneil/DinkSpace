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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHour
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHourRange
import com.thelazybattley.dinkspace.feature.courts.presentation.shortLabel
import com.thelazybattley.dinkspace.feature.courts.presentation.list.TimeRangeSelection
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.courts_time_ends_next_day
import dinkspace.shared.generated.resources.courts_time_hint_end
import dinkspace.shared.generated.resources.courts_time_hint_start
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimeRangePicker(
    selection: TimeRangeSelection,
    selectedDate: LocalDate,
    onHourClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hours = TimeRangeSelection.selectableHours.toList()
    // Open with the selected start in view, one chip of context to its left.
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selection.range.startHour - 1).coerceAtLeast(0),
    )
    val rangeLabel = formatHourRange(selection.range.startHour, selection.range.endHour)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = rangeLabel, style = MaterialTheme.typography.titleMedium)
                if (selection.range.crossesMidnight) {
                    val endDate = selection.range.datesFrom(selectedDate).last()
                    Text(
                        text = stringResource(Res.string.courts_time_ends_next_day, endDate.shortLabel()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
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
