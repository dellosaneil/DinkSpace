package com.thelazybattley.dinkspace.feature.courts.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Whole hours from [startHour] (0–23) on the selected day, end exclusive.
 * [endHour] may go past 24 to continue into the next day: 12..25 is 12 PM to 1 AM the next day.
 */
data class TimeRange(val startHour: Int, val endHour: Int) {
    init {
        require(startHour in 0 until HOURS_PER_DAY) { "startHour must be 0..23, was $startHour" }
        require(endHour in (startHour + 1)..(startHour + HOURS_PER_DAY)) {
            "endHour must be within 24 hours after startHour, was $endHour"
        }
    }

    /** Slot start hours; values of 24 and above belong to the next day. */
    val hours: IntRange get() = startHour until endHour

    val crossesMidnight: Boolean get() = endHour > HOURS_PER_DAY

    /** The days this range touches when it starts on [date]. */
    fun datesFrom(date: LocalDate): List<LocalDate> =
        if (crossesMidnight) listOf(date, date.plus(1, DateTimeUnit.DAY)) else listOf(date)

    companion object {
        const val HOURS_PER_DAY = 24
    }
}

/** Free courts per hour (0–23) at one venue on one day, as reported by the backend. */
data class DailyAvailability(
    val courtId: String,
    val date: LocalDate,
    val freeCourtsByHour: Map<Int, Int>,
)

enum class SlotStatus { OPEN, BOOKED, CLOSED }

/** One bookable hour starting at [startHour] (0–23) on [date]. */
data class HourSlot(
    val date: LocalDate,
    val startHour: Int,
    val status: SlotStatus,
    val freeCourts: Int,
)

data class CourtAvailability(
    val court: Court,
    val slots: List<HourSlot>,
) {
    val openSlotCount: Int get() = slots.count { it.status == SlotStatus.OPEN }
    val isClosed: Boolean get() = slots.all { it.status == SlotStatus.CLOSED }
}
