package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerBlock
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerContainer

/** Shimmering stand-in that mirrors the court details layout while it loads. */
@Composable
fun CourtDetailSkeleton() {
    ShimmerContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 20.dp, bottom = 20.dp),
            ) {
                ShimmerBlock(Modifier.size(96.dp), RoundedCornerShape(24.dp))
            }
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerBlock(Modifier.fillMaxWidth(0.7f).height(26.dp))
                    ShimmerBlock(Modifier.fillMaxWidth(0.5f).height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(110.dp, 140.dp, 80.dp).forEach { width ->
                            ShimmerBlock(Modifier.width(width).height(30.dp), RoundedCornerShape(50))
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShimmerBlock(Modifier.fillMaxWidth(0.45f).height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { ShimmerBlock(Modifier.width(160.dp).height(110.dp), RoundedCornerShape(14.dp)) }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShimmerBlock(Modifier.fillMaxWidth(0.4f).height(18.dp))
                    ShimmerBlock(Modifier.fillMaxWidth().height(150.dp), RoundedCornerShape(18.dp))
                }
            }
        }
    }
}
