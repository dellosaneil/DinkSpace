package com.thelazybattley.dinkspace.feature.courts.presentation

import androidx.compose.runtime.Composable
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import dinkspace.shared.generated.resources.Res
import dinkspace.shared.generated.resources.court_count
import dinkspace.shared.generated.resources.court_setting_indoor
import dinkspace.shared.generated.resources.court_setting_outdoor
import dinkspace.shared.generated.resources.weekday_fri
import dinkspace.shared.generated.resources.weekday_mon
import dinkspace.shared.generated.resources.weekday_sat
import dinkspace.shared.generated.resources.weekday_sun
import dinkspace.shared.generated.resources.weekday_thu
import dinkspace.shared.generated.resources.weekday_tue
import dinkspace.shared.generated.resources.weekday_wed
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
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

/** (16, 18) -> "4–6 PM", (11, 13) -> "11 AM–1 PM" */
fun formatHourRange(startHour: Int, endHour: Int): String {
    val start = formatHour(startHour)
    val end = formatHour(endHour)
    val startSuffix = start.substringAfter(' ')
    val endSuffix = end.substringAfter(' ')
    return if (startSuffix == endSuffix) "${start.substringBefore(' ')}–$end" else "$start–$end"
}

/** 12.0 -> "12", 12.5 -> "12.50" */
fun formatRate(rate: Double): String {
    val cents = kotlin.math.round(rate * 100).toLong()
    val whole = cents / 100
    val fraction = cents % 100
    return if (fraction == 0L) "$whole" else "$whole.${fraction.toString().padStart(2, '0')}"
}

/** "https://m.me/venue" -> "m.me/venue" */
fun String.withoutScheme(): String = substringAfter("://")

/** "Sun 11" */
@Composable
fun LocalDate.shortLabel(): String = "${stringResource(dayOfWeek.shortName())} $day"

fun DayOfWeek.shortName(): StringResource = when (this) {
    DayOfWeek.MONDAY -> Res.string.weekday_mon
    DayOfWeek.TUESDAY -> Res.string.weekday_tue
    DayOfWeek.WEDNESDAY -> Res.string.weekday_wed
    DayOfWeek.THURSDAY -> Res.string.weekday_thu
    DayOfWeek.FRIDAY -> Res.string.weekday_fri
    DayOfWeek.SATURDAY -> Res.string.weekday_sat
    DayOfWeek.SUNDAY -> Res.string.weekday_sun
}
