package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate

/**
 * One court with its hourly schedule for its opening hours on [date].
 * [slots] is null until every day the opening hours touch has been loaded.
 */
data class CourtSchedule(
    val court: Court?,
    val slots: List<HourSlot>?,
)

class ObserveCourtScheduleUseCase(
    private val courtRepository: CourtRepository,
    private val availabilityRepository: AvailabilityRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(courtId: String, date: LocalDate): Flow<CourtSchedule> =
        courtRepository.observeCourt(courtId).flatMapLatest { court ->
            val range = court?.openRange
            if (court == null || range == null) {
                flowOf(CourtSchedule(court, slots = if (court == null) null else emptyList()))
            } else {
                val perDay = range.datesFrom(date).map { availabilityRepository.observeAvailability(it) }
                combine(perDay) { days ->
                    val slots = if (days.any { it == null }) null else court.slotsFor(date, range, days.filterNotNull().flatten())
                    CourtSchedule(court, slots)
                }
            }
        }
}
