package com.thelazybattley.dinkspace.feature.courts.domain.model

import kotlinx.datetime.LocalDate

/** Whole hours, end exclusive: 16..18 covers the 4 PM and 5 PM slots. */
data class TimeRange(val startHour: Int, val endHour: Int) {
    init {
        require(startHour in 0..23) { "startHour must be 0..23, was $startHour" }
        require(endHour in (startHour + 1)..24) { "endHour must be after startHour and at most 24, was $endHour" }
    }

    val hours: IntRange get() = startHour until endHour
}

/** Free courts per hour at one venue on one day, as reported by the backend. */
data class DailyAvailability(
    val courtId: String,
    val date: LocalDate,
    val freeCourtsByHour: Map<Int, Int>,
)

enum class SlotStatus { OPEN, BOOKED, CLOSED }

/** One bookable hour starting at [startHour]. */
data class HourSlot(
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
