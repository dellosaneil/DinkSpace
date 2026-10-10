package com.thelazybattley.dinkspace.core.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Animation progress (0..1) shared by every [ShimmerBlock] inside one [ShimmerContainer]. */
private val LocalShimmerProgress = staticCompositionLocalOf<State<Float>?> { null }

private const val SHIMMER_DURATION_MS = 1_100

/**
 * Runs one shimmer animation for all placeholder blocks inside [content],
 * so their highlights sweep in sync.
 */
@Composable
fun ShimmerContainer(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )
    CompositionLocalProvider(LocalShimmerProgress provides progress, content = content)
}

/** A placeholder block with a moving highlight. Size it with [modifier]. */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    val progress = LocalShimmerProgress.current
    val base = MaterialTheme.colorScheme.outlineVariant
    val highlight = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                // Read the animation in the draw phase so frames only redraw, not recompose.
                val p = progress?.value ?: 0f
                val band = size.width.coerceAtLeast(1f)
                val x = -band + p * (size.width + 2 * band)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(base, highlight, base),
                        start = Offset(x - band / 2, 0f),
                        end = Offset(x + band / 2, size.height),
                    ),
                )
            },
    )
}
