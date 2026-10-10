package com.thelazybattley.dinkspace.feature.courts.data.remote

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

/**
 * Generates availability for [DummyCourts]. Values are derived from court, date and hour,
 * so the same day always looks the same, and roughly one in four hours is fully booked.
 */
class FakeAvailabilityRemoteDataSource : AvailabilityRemoteDataSource {

    override suspend fun fetchAvailability(date: LocalDate): Result<List<AvailabilityDto>, DataError.Remote> {
        delay(FAKE_LATENCY_MS)
        return Result.Success(
            DummyCourts.all.map { court ->
                AvailabilityDto(
                    courtId = court.id,
                    date = date.toString(),
                    freeCourtsByHour = (court.openingHour until court.closingHour).associate { hour ->
                        hour.toString() to freeCourts(court, date, hour)
                    },
                )
            },
        )
    }

    private fun freeCourts(court: CourtDto, date: LocalDate, hour: Int): Int {
        val seed = mix(court.id.hashCode() xor mix(date.toEpochDays().toInt() * 24 + hour))
        return if (seed % 4 == 0) 0 else 1 + (seed / 4) % court.courtCount
    }

    /** Integer hash that spreads nearby inputs apart, so booked hours don't fall into a pattern. */
    private fun mix(value: Int): Int {
        var x = value
        x = (x xor (x ushr 16)) * 0x45d9f3b
        x = (x xor (x ushr 16)) * 0x45d9f3b
        x = x xor (x ushr 16)
        return x and Int.MAX_VALUE
    }

    private companion object {
        const val FAKE_LATENCY_MS = 400L
    }
}
