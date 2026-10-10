package com.thelazybattley.dinkspace.feature.courts.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.feature.courts.presentation.TimeField
import com.thelazybattley.dinkspace.feature.courts.presentation.TimeRangeSelection
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHour
import com.thelazybattley.dinkspace.feature.courts.presentation.shortLabel
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.action_done
import dinkspace.shared.generated.resources.courts_time_ends_next_day
import dinkspace.shared.generated.resources.ic_back
import dinkspace.shared.generated.resources.ic_chevron_down
import dinkspace.shared.generated.resources.time_end_hint
import dinkspace.shared.generated.resources.time_end_title
import dinkspace.shared.generated.resources.time_from
import dinkspace.shared.generated.resources.time_next_day
import dinkspace.shared.generated.resources.time_start_hint
import dinkspace.shared.generated.resources.time_start_title
import dinkspace.shared.generated.resources.time_to
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val PANEL_COLUMNS = 4

/**
 * FROM → TO boxes. Tapping a box opens an hour panel for it; picking an hour sets that time
 * and closes the panel. An end time at or before the start is labelled "next day".
 */
@Composable
fun TimeRangePicker(
    selection: TimeRangeSelection,
    selectedDate: LocalDate,
    onFieldClick: (TimeField) -> Unit,
    onHourPick: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val range = selection.range
    Column(modifier = modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            TimeBox(
                label = stringResource(Res.string.time_from),
                value = formatHour(range.startHour),
                isOpen = selection.openField == TimeField.START,
                onClick = { onFieldClick(TimeField.START) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.size(18.dp).rotate(180f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TimeBox(
                label = stringResource(Res.string.time_to),
                value = formatHour(range.endHour),
                caption = if (range.crossesMidnight) {
                    stringResource(Res.string.courts_time_ends_next_day, range.datesFrom(selectedDate).last().shortLabel())
                } else {
                    null
                },
                isOpen = selection.openField == TimeField.END,
                onClick = { onFieldClick(TimeField.END) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        AnimatedVisibility(
            visible = selection.openField != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            HourPanel(selection = selection, onHourPick = onHourPick, onDismiss = onDismiss)
        }
    }
}

@Composable
private fun TimeBox(
    label: String,
    value: String,
    isOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    caption: String? = null,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 62.dp)
            .semantics { stateDescription = if (isOpen) "Expanded" else "Collapsed" },
        shape = RoundedCornerShape(12.dp),
        color = if (isOpen) colors.primaryContainer else colors.surface,
        contentColor = if (isOpen) colors.onPrimaryContainer else colors.onSurface,
        border = if (isOpen) BorderStroke(2.dp, colors.primary) else BorderStroke(1.dp, colors.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOpen) colors.onPrimaryContainer else colors.onSurfaceVariant,
                )
                Text(text = value, style = MaterialTheme.typography.titleMedium)
                caption?.let {
                    Text(text = it, style = MaterialTheme.typography.labelSmall, color = colors.primary)
                }
            }
            // Chevron: down when closed, up when the panel is open.
            Icon(
                painter = painterResource(Res.drawable.ic_chevron_down),
                contentDescription = null,
                modifier = Modifier.size(20.dp).rotate(if (isOpen) 180f else 0f),
                tint = if (isOpen) colors.primary else colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HourPanel(
    selection: TimeRangeSelection,
    onHourPick: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val isStart = selection.openField == TimeField.START
    Surface(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(if (isStart) Res.string.time_start_title else Res.string.time_end_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = stringResource(if (isStart) Res.string.time_start_hint else Res.string.time_end_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_done)) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TimeRangeSelection.selectableHours.chunked(PANEL_COLUMNS).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { hour ->
                            HourOption(
                                label = formatHour(hour),
                                isSelected = selection.isSelected(hour),
                                isNextDay = selection.isNextDay(hour),
                                onClick = { onHourPick(hour) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(PANEL_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HourOption(
    label: String,
    isSelected: Boolean,
    isNextDay: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .semantics {
                role = Role.Button
                selected = isSelected
            },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) colors.primary else colors.surface,
        contentColor = if (isSelected) colors.onPrimary else colors.onSurface,
        border = if (isSelected) null else BorderStroke(1.dp, colors.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            if (isNextDay) {
                Text(
                    text = stringResource(Res.string.time_next_day),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) colors.onPrimary else colors.primary,
                )
            }
        }
    }
}
