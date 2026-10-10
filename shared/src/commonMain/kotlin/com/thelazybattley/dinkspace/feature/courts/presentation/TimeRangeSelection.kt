package com.thelazybattley.dinkspace.feature.courts.presentation

import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange.Companion.HOURS_PER_DAY

enum class TimeField { START, END }

/**
 * FROM/TO picker state. Tapping a field opens an hour panel for it; picking an hour sets that end
 * of the range and closes the panel, so a tap never changes the other field by surprise.
 * An end at or before the start runs into the next day (12 PM to 1 AM = 12..25).
 */
data class TimeRangeSelection(
    val range: TimeRange = TimeRange(startHour = 16, endHour = 18),
    val openField: TimeField? = null,
) {
    fun toggle(field: TimeField): TimeRangeSelection = copy(openField = if (openField == field) null else field)

    fun close(): TimeRangeSelection = copy(openField = null)

    fun pick(hour: Int): TimeRangeSelection {
        val tapped = hour.mod(HOURS_PER_DAY)
        val newRange = when (openField) {
            TimeField.START -> {
                // Keep the chosen end time; it moves to the next day if it no longer follows the start.
                val endOfDay = range.endHour % HOURS_PER_DAY
                val end = when {
                    endOfDay == tapped -> tapped + 1
                    endOfDay > tapped -> endOfDay
                    else -> endOfDay + HOURS_PER_DAY
                }
                TimeRange(tapped, end)
            }
            TimeField.END -> TimeRange(
                startHour = range.startHour,
                endHour = if (tapped > range.startHour) tapped else tapped + HOURS_PER_DAY,
            )
            null -> return this
        }
        return TimeRangeSelection(newRange, openField = null)
    }

    /** The hour currently set for the open field, highlighted in the panel. */
    fun isSelected(hour: Int): Boolean = when (openField) {
        TimeField.START -> hour == range.startHour
        TimeField.END -> hour == range.endHour % HOURS_PER_DAY
        null -> false
    }

    /** End hours at or before the start finish after midnight. */
    fun isNextDay(hour: Int): Boolean = openField == TimeField.END && hour <= range.startHour

    companion object {
        val selectableHours: IntRange = 0 until HOURS_PER_DAY
    }
}
