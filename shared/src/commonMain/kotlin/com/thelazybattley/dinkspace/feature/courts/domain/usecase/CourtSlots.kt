package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Hourly slots for this court across [range] starting on [startDate]. Hours of 24 and above
 * read the next day's availability. A missing hour means nothing is booked yet.
 */
internal fun Court.slotsFor(
    startDate: LocalDate,
    range: TimeRange,
    availability: List<DailyAvailability>,
): List<HourSlot> {
    val byDate = availability.filter { it.courtId == id }.associateBy { it.date }
    val nextDate = startDate.plus(1, DateTimeUnit.DAY)
    return range.hours.map { hour ->
        val date = if (hour >= TimeRange.HOURS_PER_DAY) nextDate else startDate
        val hourOfDay = hour % TimeRange.HOURS_PER_DAY
        if (!isOpenAt(hourOfDay)) {
            HourSlot(date, hourOfDay, SlotStatus.CLOSED, freeCourts = 0)
        } else {
            val free = (byDate[date]?.freeCourtsByHour?.get(hourOfDay) ?: courtCount).coerceIn(0, courtCount)
            HourSlot(date, hourOfDay, if (free > 0) SlotStatus.OPEN else SlotStatus.BOOKED, free)
        }
    }
}
