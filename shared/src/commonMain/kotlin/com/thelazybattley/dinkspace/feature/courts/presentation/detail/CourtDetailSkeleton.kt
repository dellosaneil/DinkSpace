package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerBlock
import com.thelazybattley.dinkspace.core.presentation.components.ShimmerContainer

/** Shimmering stand-in for the court details content while it loads; the header shows its own. */
@Composable
fun CourtDetailSkeleton() {
    ShimmerContainer {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBlock(Modifier.fillMaxWidth().height(196.dp), RoundedCornerShape(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(4) { ShimmerBlock(Modifier.weight(1f).height(52.dp), RoundedCornerShape(10.dp)) }
                }
            }
            ShimmerBlock(Modifier.fillMaxWidth().height(300.dp), RoundedCornerShape(16.dp))
            ShimmerBlock(Modifier.width(180.dp).height(20.dp))
        }
    }
}

/** Placeholder rows for the availability table while a day loads. */
@Composable
fun ScheduleTableSkeleton() {
    ShimmerContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(6) { ShimmerBlock(Modifier.fillMaxWidth().height(44.dp), RoundedCornerShape(10.dp)) }
        }
    }
}
