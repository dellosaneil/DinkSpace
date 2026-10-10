package com.thelazybattley.dinkspace.feature.courts.presentation.list.components

import androidx.compose.animation.core.animate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.courts_show_filters
import dinkspace.shared.generated.resources.ic_chevron_down
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Courts header whose [filters] slide away as the results scroll, leaving a compact toolbar
 * with the [title] and the [summary]. Once collapsed, tapping the summary row brings the
 * filters back. Drive it with an exit-until-collapsed [scrollBehavior] whose nested scroll
 * connection sits on the results.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingCourtsHeader(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    filters: @Composable () -> Unit,
    summary: @Composable () -> Unit,
) {
    val state = scrollBehavior.state
    val fraction = state.collapsedFraction
    val scope = rememberCoroutineScope()
    val expand: () -> Unit = {
        scope.launch { animate(state.heightOffset, 0f) { value, _ -> state.heightOffset = value } }
    }

    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = lerp(16.dp, 12.dp, fraction),
                    bottom = lerp(16.dp, 8.dp, fraction),
                ),
                style = lerp(MaterialTheme.typography.headlineMedium, MaterialTheme.typography.titleLarge, fraction),
            )

            Layout(
                content = filters,
                modifier = Modifier.clipToBounds(),
            ) { measurables, constraints ->
                val placeable = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Int.MAX_VALUE))
                // The filters change height (the hour panel opens and closes), so the collapse range follows them.
                val limit = -placeable.height.toFloat()
                if (state.heightOffsetLimit != limit) state.heightOffsetLimit = limit
                val offset = state.heightOffset.roundToInt().coerceIn(-placeable.height, 0)
                layout(placeable.width, placeable.height + offset) {
                    placeable.place(0, offset)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = fraction > 0f, onClick = expand)
                    .padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) { summary() }
                Icon(
                    painter = painterResource(Res.drawable.ic_chevron_down),
                    contentDescription = stringResource(Res.string.courts_show_filters),
                    modifier = Modifier.size(20.dp).alpha(fraction),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            HorizontalDivider(
                modifier = Modifier.alpha(fraction),
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
