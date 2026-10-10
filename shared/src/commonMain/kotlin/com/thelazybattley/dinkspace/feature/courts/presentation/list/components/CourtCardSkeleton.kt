package com.thelazybattley.dinkspace.feature.courts.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerBlock
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerContainer

private const val SKELETON_CARDS = 4

/** Shimmering stand-in for the court list while it loads. */
@Composable
fun CourtListSkeleton(showSlots: Boolean) {
    ShimmerContainer {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false,
        ) {
            items(SKELETON_CARDS) { CourtCardSkeleton(showSlots) }
        }
    }
}

@Composable
private fun CourtCardSkeleton(showSlots: Boolean) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                ShimmerBlock(Modifier.fillMaxWidth(0.55f).height(18.dp))
                if (showSlots) ShimmerBlock(Modifier.size(width = 64.dp, height = 22.dp), RoundedCornerShape(50))
            }
            ShimmerBlock(Modifier.fillMaxWidth(0.45f).height(14.dp))
            if (showSlots) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) { ShimmerBlock(Modifier.weight(1f).height(48.dp), RoundedCornerShape(12.dp)) }
                }
            }
            ShimmerBlock(Modifier.fillMaxWidth(0.4f).height(14.dp))
        }
    }
}
