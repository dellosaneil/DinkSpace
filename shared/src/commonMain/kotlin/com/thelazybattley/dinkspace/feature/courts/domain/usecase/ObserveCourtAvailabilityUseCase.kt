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
import kotlinx.datetime.LocalDate

/**
 * Every court with its hourly slots inside [TimeRange] on a date, courts with the most open
 * slots first. Emits null while that date's availability hasn't been loaded yet.
 */
class ObserveCourtAvailabilityUseCase(
    private val courtRepository: CourtRepository,
    private val availabilityRepository: AvailabilityRepository,
) {
    operator fun invoke(date: LocalDate, range: TimeRange): Flow<List<CourtAvailability>?> =
        combine(
            courtRepository.observeCourts(),
            availabilityRepository.observeAvailability(date),
        ) { courts, availability ->
            availability?.let { buildAvailability(courts, it, range) }
        }

    private fun buildAvailability(
        courts: List<Court>,
        availability: List<DailyAvailability>,
        range: TimeRange,
    ): List<CourtAvailability> {
        val byCourt = availability.associateBy { it.courtId }
        return courts
            .map { court ->
                val freeByHour = byCourt[court.id]?.freeCourtsByHour.orEmpty()
                CourtAvailability(
                    court = court,
                    slots = range.hours.map { hour -> slotFor(court, hour, freeByHour) },
                )
            }
            .sortedWith(compareByDescending<CourtAvailability> { it.openSlotCount }.thenBy { it.court.name })
    }

    private fun slotFor(court: Court, hour: Int, freeByHour: Map<Int, Int>): HourSlot {
        if (hour < court.openingHour || hour >= court.closingHour) {
            return HourSlot(hour, SlotStatus.CLOSED, freeCourts = 0)
        }
        // A missing hour means nothing is booked yet.
        val free = (freeByHour[hour] ?: court.courtCount).coerceIn(0, court.courtCount)
        return HourSlot(hour, if (free > 0) SlotStatus.OPEN else SlotStatus.BOOKED, free)
    }
}
