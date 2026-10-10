package com.thelazybattley.dinkspace.feature.courts.presentation.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerBlock
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerContainer
import com.thelazybattley.dinkspace.feature.courts.presentation.components.VenueLogo
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.action_back
import dinkspace.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val CollapsedHeight = 64.dp
private val ExpandedHeight = 172.dp

// Logo and title positions at each end of the collapse; everything in between is interpolated.
private val LogoSizeExpanded = 72.dp
private val LogoSizeCollapsed = 36.dp
private val LogoStartExpanded = 20.dp
private val LogoStartCollapsed = 56.dp
private val LogoTopExpanded = 76.dp
private val LogoTopCollapsed = (CollapsedHeight - LogoSizeCollapsed) / 2
private val TitleTopExpanded = 88.dp
private val TitleTopCollapsed = 20.dp

/**
 * Venue header that starts large (back button above a big logo and name) and shrinks into a
 * one-row toolbar (back, small logo, name) as the content scrolls. Drive it with an
 * exit-until-collapsed [scrollBehavior] whose nested scroll connection is on the screen.
 * Pass a null [name] while the venue loads to show placeholders.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingCourtHeader(
    name: String?,
    subtitle: String,
    logoUrl: String?,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
) {
    val density = LocalDensity.current
    val collapseRange = with(density) { (ExpandedHeight - CollapsedHeight).toPx() }
    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != -collapseRange) {
            scrollBehavior.state.heightOffsetLimit = -collapseRange
        }
    }
    val fraction = scrollBehavior.state.collapsedFraction
    val height = ExpandedHeight + with(density) { scrollBehavior.state.heightOffset.toDp() }

    val logoSize = lerp(LogoSizeExpanded, LogoSizeCollapsed, fraction)
    val logoStart = lerp(LogoStartExpanded, LogoStartCollapsed, fraction)
    val logoTop = lerp(LogoTopExpanded, LogoTopCollapsed, fraction)
    val titleStart = logoStart + logoSize + lerp(14.dp, 12.dp, fraction)
    val titleTop = lerp(TitleTopExpanded, TitleTopCollapsed, fraction)
    val titleStyle = lerp(MaterialTheme.typography.headlineSmall, MaterialTheme.typography.titleMedium, fraction)

    Surface(color = MaterialTheme.colorScheme.surface) {
        Box(
            modifier = Modifier
                .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                .fillMaxWidth()
                .height(height)
                .clipToBounds(),
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
            ) {
                Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.action_back))
            }

            if (name == null) {
                ShimmerContainer {
                    ShimmerBlock(
                        modifier = Modifier.padding(start = logoStart, top = logoTop).size(logoSize),
                        shape = RoundedCornerShape(logoSize / 4),
                    )
                    ShimmerBlock(
                        modifier = Modifier.padding(start = titleStart, top = titleTop + 4.dp).width(180.dp).height(22.dp),
                    )
                }
            } else {
                VenueLogo(
                    name = name,
                    logoUrl = logoUrl,
                    size = logoSize,
                    modifier = Modifier.padding(start = logoStart, top = logoTop),
                )
                Column(modifier = Modifier.padding(start = titleStart, top = titleTop, end = 16.dp)) {
                    Text(text = name, style = titleStyle, maxLines = if (fraction > 0.5f) 1 else 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        text = subtitle,
                        modifier = Modifier.alpha((1f - fraction * 2f).coerceIn(0f, 1f)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomStart).alpha(fraction),
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
