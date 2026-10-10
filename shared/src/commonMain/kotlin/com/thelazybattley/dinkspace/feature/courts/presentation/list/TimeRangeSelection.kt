package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange

/**
 * Two-tap range picker: the first tap sets the start (and a one-hour range),
 * the second tap sets the end. Tapping at or before the start restarts the selection.
 */
data class TimeRangeSelection(
    val range: TimeRange = TimeRange(startHour = 16, endHour = 18),
    val isPickingEnd: Boolean = false,
) {
    fun onHourClick(hour: Int): TimeRangeSelection {
        val clamped = hour.coerceIn(FIRST_HOUR, LAST_HOUR)
        return if (!isPickingEnd || clamped <= range.startHour) {
            val start = clamped.coerceAtMost(LAST_HOUR - 1)
            TimeRangeSelection(TimeRange(start, start + 1), isPickingEnd = true)
        } else {
            TimeRangeSelection(TimeRange(range.startHour, clamped), isPickingEnd = false)
        }
    }

    /** The two ends of the range get the strong highlight; while picking the end only the start does. */
    fun isEdge(hour: Int): Boolean = hour == range.startHour || (!isPickingEnd && hour == range.endHour)

    fun isBetween(hour: Int): Boolean = !isPickingEnd && hour > range.startHour && hour < range.endHour

    companion object {
        const val FIRST_HOUR = 6
        const val LAST_HOUR = 22
        val selectableHours: IntRange = FIRST_HOUR..LAST_HOUR
    }
}
