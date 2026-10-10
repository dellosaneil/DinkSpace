package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Every court with its hourly slots inside [TimeRange] starting on a date, courts with the most
 * open slots first. A range past midnight reads the next day's availability for those hours.
 * Emits null until every day the range touches has been loaded.
 */
class ObserveCourtAvailabilityUseCase(
    private val courtRepository: CourtRepository,
    private val availabilityRepository: AvailabilityRepository,
) {
    operator fun invoke(date: LocalDate, range: TimeRange): Flow<List<CourtAvailability>?> {
        val perDay = range.datesFrom(date).map { availabilityRepository.observeAvailability(it) }
        return combine(courtRepository.observeCourts(), combine(perDay) { it.toList() }) { courts, days ->
            if (days.any { it == null }) null else buildAvailability(courts, days.filterNotNull().flatten(), date, range)
        }
    }

    private fun buildAvailability(
        courts: List<Court>,
        availability: List<DailyAvailability>,
        startDate: LocalDate,
        range: TimeRange,
    ): List<CourtAvailability> {
        val byCourtAndDate = availability.associateBy { it.courtId to it.date }
        val nextDate = startDate.plus(1, DateTimeUnit.DAY)
        return courts
            .map { court ->
                CourtAvailability(
                    court = court,
                    slots = range.hours.map { hour ->
                        val date = if (hour >= TimeRange.HOURS_PER_DAY) nextDate else startDate
                        val hourOfDay = hour % TimeRange.HOURS_PER_DAY
                        val freeByHour = byCourtAndDate[court.id to date]?.freeCourtsByHour.orEmpty()
                        slotFor(court, date, hourOfDay, freeByHour)
                    },
                )
            }
            .sortedWith(compareByDescending<CourtAvailability> { it.openSlotCount }.thenBy { it.court.name })
    }

    private fun slotFor(court: Court, date: LocalDate, hourOfDay: Int, freeByHour: Map<Int, Int>): HourSlot {
        if (!court.isOpenAt(hourOfDay)) {
            return HourSlot(date, hourOfDay, SlotStatus.CLOSED, freeCourts = 0)
        }
        // A missing hour means nothing is booked yet.
        val free = (freeByHour[hourOfDay] ?: court.courtCount).coerceIn(0, court.courtCount)
        return HourSlot(date, hourOfDay, if (free > 0) SlotStatus.OPEN else SlotStatus.BOOKED, free)
    }
}
