package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.core.domain.Result
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObserveCourtAvailabilityUseCaseTest {

    private val date = LocalDate(2026, 10, 10)
    private val earlyBird = court(id = "early", name = "Early Bird", opening = 6, closing = 17)
    private val nightOwl = court(id = "night", name = "Night Owl", opening = 6, closing = 22)

    @Test
    fun emitsNullUntilTheDayIsLoaded() = runTest {
        val useCase = useCase(availability = null)

        assertNull(useCase(date, TimeRange(16, 18)).first())
    }

    @Test
    fun marksSlotsOpenBookedOrClosed() = runTest {
        val useCase = useCase(
            availability = listOf(
                DailyAvailability("early", date, mapOf(16 to 0)),
                DailyAvailability("night", date, mapOf(16 to 2, 17 to 0)),
            ),
        )

        val result = useCase(date, TimeRange(16, 18)).first()!!.associateBy { it.court.id }

        assertEquals(listOf(SlotStatus.BOOKED, SlotStatus.CLOSED), result.getValue("early").slots.map { it.status })
        assertEquals(listOf(SlotStatus.OPEN, SlotStatus.BOOKED), result.getValue("night").slots.map { it.status })
    }

    @Test
    fun missingHoursCountAsFullyFree() = runTest {
        val useCase = useCase(availability = listOf(DailyAvailability("night", date, emptyMap())))

        val night = useCase(date, TimeRange(18, 19)).first()!!.first { it.court.id == "night" }

        assertEquals(SlotStatus.OPEN, night.slots.single().status)
        assertEquals(nightOwl.courtCount, night.slots.single().freeCourts)
    }

    @Test
    fun sortsCourtsWithMoreOpenSlotsFirst() = runTest {
        val useCase = useCase(
            availability = listOf(
                DailyAvailability("early", date, mapOf(16 to 0)),
                DailyAvailability("night", date, mapOf(16 to 3)),
            ),
        )

        val ids = useCase(date, TimeRange(16, 18)).first()!!.map { it.court.id }

        assertEquals(listOf("night", "early"), ids)
    }

    private fun useCase(availability: List<DailyAvailability>?) = ObserveCourtAvailabilityUseCase(
        courtRepository = object : CourtRepository {
            override fun observeCourts(): Flow<List<Court>> = flowOf(listOf(earlyBird, nightOwl))
            override fun observeCourt(id: String): Flow<Court?> = flowOf(null)
            override suspend fun refreshCourts(): EmptyResult<DataError> = Result.Success(Unit)
        },
        availabilityRepository = object : AvailabilityRepository {
            override fun observeAvailability(date: LocalDate) = MutableStateFlow(availability)
            override suspend fun refreshAvailability(date: LocalDate): EmptyResult<DataError> = Result.Success(Unit)
        },
    )

    private fun court(id: String, name: String, opening: Int, closing: Int) = Court(
        id = id,
        name = name,
        area = "",
        address = "",
        courtCount = 4,
        setting = CourtSetting.OUTDOOR,
        openingHour = opening,
        closingHour = closing,
        contact = CourtContact(phoneNumber = "000", messengerUrl = null, email = null),
        logoUrl = null,
        photoUrls = emptyList(),
        hourlyRate = null,
    )
}
