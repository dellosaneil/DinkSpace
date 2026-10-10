package com.thelazybattley.dinkspace.feature.courts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.feature.courts.presentation.shortName
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.date_today
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/** Row of upcoming days; the first one is labelled "Today". */
@Composable
fun DateChips(
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
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
