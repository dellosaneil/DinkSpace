package com.thelazybattley.dinkspace.feature.courts.presentation

import androidx.compose.runtime.Composable
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.court_count
import dinkspace.shared.generated.resources.court_setting_indoor
import dinkspace.shared.generated.resources.court_setting_outdoor
import org.jetbrains.compose.resources.stringResource

/** "6 outdoor courts" */
@Composable
fun Court.courtSummary(): String {
    val setting = when (setting) {
        CourtSetting.INDOOR -> stringResource(Res.string.court_setting_indoor)
        CourtSetting.OUTDOOR -> stringResource(Res.string.court_setting_outdoor)
    }
    return stringResource(Res.string.court_count, courtCount, setting)
}

/** 6 -> "6 AM", 13 -> "1 PM", 0 or 24 -> "12 AM" */
fun formatHour(hour: Int): String {
    val normalized = hour.mod(24)
    val display = if (normalized % 12 == 0) 12 else normalized % 12
    val suffix = if (normalized < 12) "AM" else "PM"
    return "$display $suffix"
}
