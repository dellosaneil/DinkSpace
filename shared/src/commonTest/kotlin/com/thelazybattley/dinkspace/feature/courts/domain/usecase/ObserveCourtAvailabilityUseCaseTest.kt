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

    @Test
    fun rangePastMidnightReadsTheNextDay() = runTest {
        val nextDay = LocalDate(2026, 10, 11)
        val lateNight = court(id = "late", name = "Late", opening = 6, closing = 26)
        val useCase = useCase(
            availabilityByDate = mapOf(
                date to listOf(DailyAvailability("late", date, mapOf(23 to 1))),
                nextDay to listOf(DailyAvailability("late", nextDay, mapOf(0 to 0))),
            ),
            courts = listOf(lateNight),
        )

        val slots = useCase(date, TimeRange(23, 25)).first()!!.single().slots

        assertEquals(listOf(date to 23, nextDay to 0), slots.map { it.date to it.startHour })
        assertEquals(listOf(SlotStatus.OPEN, SlotStatus.BOOKED), slots.map { it.status })
    }

    @Test
    fun rangePastMidnightWaitsForTheNextDay() = runTest {
        val useCase = useCase(availabilityByDate = mapOf(date to emptyList()))

        assertNull(useCase(date, TimeRange(23, 25)).first())
    }

    @Test
    fun venueOpenPastMidnightIsOpenInTheEarlyHours() = runTest {
        val lateNight = court(id = "late", name = "Late", opening = 6, closing = 26)
        val useCase = useCase(availability = emptyList(), courts = listOf(lateNight))

        val slots = useCase(date, TimeRange(0, 3)).first()!!.single().slots

        assertEquals(listOf(SlotStatus.OPEN, SlotStatus.OPEN, SlotStatus.CLOSED), slots.map { it.status })
    }

    @Test
    fun queryNarrowsCourtsAlongsideTheTimeRange() = runTest {
        val useCase = useCase(availability = emptyList())

        val result = useCase(date, TimeRange(16, 18), query = "night").first()!!

        assertEquals(listOf("night"), result.map { it.court.id })
        assertEquals(2, result.single().slots.size)
    }

    private fun useCase(availability: List<DailyAvailability>?, courts: List<Court> = listOf(earlyBird, nightOwl)) =
        useCase(availabilityByDate = availability?.let { mapOf(date to it) }.orEmpty(), courts = courts)

    private fun useCase(
        availabilityByDate: Map<LocalDate, List<DailyAvailability>>,
        courts: List<Court> = listOf(earlyBird, nightOwl),
    ) = ObserveCourtAvailabilityUseCase(
        searchCourts = SearchCourtsUseCase(
            object : CourtRepository {
                override fun observeCourts(): Flow<List<Court>> = flowOf(courts)
                override fun observeCourt(id: String): Flow<Court?> = flowOf(null)
                override suspend fun refreshCourts(): EmptyResult<DataError> = Result.Success(Unit)
            },
        ),
        availabilityRepository = object : AvailabilityRepository {
            override fun observeAvailability(date: LocalDate) = MutableStateFlow(availabilityByDate[date])
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
        contact = CourtContact(phoneNumber = "000", messengerUrl = null),
        logoUrl = null,
        photoUrls = emptyList(),
        rates = null,
    )
}
