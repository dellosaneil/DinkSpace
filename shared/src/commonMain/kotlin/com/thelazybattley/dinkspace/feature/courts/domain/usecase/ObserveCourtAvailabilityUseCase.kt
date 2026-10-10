package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

/**
 * Courts matching [query] (name or area, blank for all) with their hourly slots inside [TimeRange]
 * starting on a date, courts with the most open slots first. A range past midnight reads the next day's availability for those hours.
 * Emits null until every day the range touches has been loaded.
 */
class ObserveCourtAvailabilityUseCase(
    private val searchCourts: SearchCourtsUseCase,
    private val availabilityRepository: AvailabilityRepository,
) {
    operator fun invoke(date: LocalDate, range: TimeRange, query: String = ""): Flow<List<CourtAvailability>?> {
        val perDay = range.datesFrom(date).map { availabilityRepository.observeAvailability(it) }
        return combine(searchCourts(query), combine(perDay) { it.toList() }) { courts, days ->
            if (days.any { it == null }) null else buildAvailability(courts, days.filterNotNull().flatten(), date, range)
        }
    }

    private fun buildAvailability(
        courts: List<Court>,
        availability: List<DailyAvailability>,
        startDate: LocalDate,
        range: TimeRange,
    ): List<CourtAvailability> =
        courts
            .map { court -> CourtAvailability(court, court.slotsFor(startDate, range, availability)) }
            .sortedWith(compareByDescending<CourtAvailability> { it.openSlotCount }.thenBy { it.court.name })
}
