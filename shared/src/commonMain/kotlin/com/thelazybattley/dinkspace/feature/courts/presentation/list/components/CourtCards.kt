package com.thelazybattley.dinkspace.feature.courts.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
import com.thelazybattley.dinkspace.feature.courts.presentation.courtSummary
import com.thelazybattley.dinkspace.feature.courts.presentation.formatHourRange
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.court_view_details
import dinkspace.shared.generated.resources.courts_closed
import dinkspace.shared.generated.resources.courts_fully_booked
import dinkspace.shared.generated.resources.courts_open_slots
import dinkspace.shared.generated.resources.ic_courts
import dinkspace.shared.generated.resources.slot_booked
import dinkspace.shared.generated.resources.slot_closed
import dinkspace.shared.generated.resources.slot_open
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val SLOT_COLUMNS = 3

@Composable
fun CourtCard(
    court: Court,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CourtCardFrame(court = court, onClick = onClick, modifier = modifier)
}

@Composable
fun CourtAvailabilityCard(
    availability: CourtAvailability,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CourtCardFrame(
        court = availability.court,
        onClick = onClick,
        modifier = modifier,
        badge = { AvailabilityBadge(availability) },
    ) {
        SlotGrid(availability.slots)
    }
}

@Composable
private fun CourtCardFrame(
    court: Court,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: @Composable () -> Unit = {},
    body: @Composable () -> Unit = {},
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = court.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    badge()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_courts),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${court.area} · ${court.courtSummary()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            body()
            Text(
                text = stringResource(Res.string.court_view_details) + " →",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun AvailabilityBadge(availability: CourtAvailability) {
    val colors = MaterialTheme.colorScheme
    val (text, container, content) = when {
        availability.openSlotCount > 0 -> Triple(
            stringResource(Res.string.courts_open_slots, availability.openSlotCount),
            colors.primaryContainer,
            colors.onPrimaryContainer,
        )
        availability.isClosed -> Triple(stringResource(Res.string.courts_closed), colors.surfaceVariant, colors.onSurfaceVariant)
        else -> Triple(stringResource(Res.string.courts_fully_booked), colors.surfaceVariant, colors.onSurfaceVariant)
    }
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 8.dp)
            .background(container, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = content,
    )
}

@Composable
private fun SlotGrid(slots: List<HourSlot>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        slots.chunked(SLOT_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { slot -> SlotTile(slot, Modifier.weight(1f)) }
                repeat(SLOT_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SlotTile(slot: HourSlot, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val isOpen = slot.status == SlotStatus.OPEN
    val container = if (isOpen) colors.primaryContainer else colors.surfaceVariant
    val timeColor = if (isOpen) colors.onPrimaryContainer else colors.onSurfaceVariant
    val statusText = stringResource(
        when (slot.status) {
            SlotStatus.OPEN -> Res.string.slot_open
            SlotStatus.BOOKED -> Res.string.slot_booked
            SlotStatus.CLOSED -> Res.string.slot_closed
        },
    )
    Column(
        modifier = modifier
            .background(container, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = formatHourRange(slot.startHour, slot.startHour + 1),
            style = MaterialTheme.typography.labelLarge,
            color = timeColor,
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            StatusDot(filled = isOpen, color = if (isOpen) colors.primary else colors.onSurfaceVariant)
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isOpen) colors.primary else colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatusDot(filled: Boolean, color: Color) {
    val dot = Modifier.size(7.dp)
    Box(
        modifier = if (filled) {
            dot.background(color, CircleShape)
        } else {
            dot.border(1.5.dp, color, CircleShape)
        },
    )
}
