package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange.Companion.HOURS_PER_DAY

/**
 * Two-tap range picker over a 24-hour strip. The first tap sets the start (and a one-hour range),
 * the second sets the end. An end earlier than the start wraps into the next day, so
 * 12 PM then 1 AM selects 12 PM to 1 AM the next day. Tapping the start again restarts there.
 */
data class TimeRangeSelection(
    val range: TimeRange = TimeRange(startHour = 16, endHour = 18),
    val isPickingEnd: Boolean = false,
) {
    fun onHourClick(hour: Int): TimeRangeSelection {
        val tapped = hour.mod(HOURS_PER_DAY)
        val start = range.startHour
        return if (!isPickingEnd || tapped == start) {
            TimeRangeSelection(TimeRange(tapped, tapped + 1), isPickingEnd = true)
        } else {
            val end = if (tapped > start) tapped else tapped + HOURS_PER_DAY
            TimeRangeSelection(TimeRange(start, end), isPickingEnd = false)
        }
    }

    /** The two ends of the range get the strong highlight; while picking the end only the start does. */
    fun isEdge(hour: Int): Boolean =
        hour == range.startHour || (!isPickingEnd && hour == range.endHour % HOURS_PER_DAY)

    /** Hours strictly inside the range, wrapping past midnight. */
    fun isBetween(hour: Int): Boolean =
        !isPickingEnd && ((range.startHour + 1) until range.endHour).any { it % HOURS_PER_DAY == hour } && !isEdge(hour)

    companion object {
        val selectableHours: IntRange = 0 until HOURS_PER_DAY
    }
}
