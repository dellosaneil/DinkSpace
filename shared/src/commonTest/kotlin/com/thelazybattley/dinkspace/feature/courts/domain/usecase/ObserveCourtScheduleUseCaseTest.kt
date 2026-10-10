package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.core.domain.Result
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.model.SlotStatus
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

class ObserveCourtScheduleUseCaseTest {

    private val date = LocalDate(2026, 10, 10)
    private val nextDay = LocalDate(2026, 10, 11)

    @Test
    fun coversTheOpeningHours() = runTest {
        val court = court(opening = 6, closing = 10)
        val schedule = useCase(court, mapOf(date to listOf(DailyAvailability("c", date, mapOf(7 to 0))))).invoke("c", date).first()

        assertEquals(listOf(6, 7, 8, 9), schedule.slots!!.map { it.startHour })
        assertEquals(SlotStatus.BOOKED, schedule.slots!![1].status)
    }

    @Test
    fun lateNightVenueReadsTheNextDay() = runTest {
        val court = court(opening = 22, closing = 26)
        val schedule = useCase(
            court,
            mapOf(date to emptyList(), nextDay to listOf(DailyAvailability("c", nextDay, mapOf(1 to 0)))),
        ).invoke("c", date).first()

        assertEquals(listOf(date, date, nextDay, nextDay), schedule.slots!!.map { it.date })
        assertEquals(SlotStatus.BOOKED, schedule.slots!![3].status)
    }

    @Test
    fun slotsAreNullUntilEveryDayLoads() = runTest {
        val court = court(opening = 22, closing = 26)
        val schedule = useCase(court, mapOf(date to emptyList())).invoke("c", date).first()

        assertEquals(court, schedule.court)
        assertNull(schedule.slots)
    }

    @Test
    fun missingCourtHasNoSchedule() = runTest {
        val schedule = useCase(court = null, availability = emptyMap()).invoke("c", date).first()

        assertNull(schedule.court)
        assertNull(schedule.slots)
    }

    private fun useCase(court: Court?, availability: Map<LocalDate, List<DailyAvailability>>) = ObserveCourtScheduleUseCase(
        courtRepository = object : CourtRepository {
            override fun observeCourts(): Flow<List<Court>> = flowOf(listOfNotNull(court))
            override fun observeCourt(id: String): Flow<Court?> = flowOf(court)
            override suspend fun refreshCourts(): EmptyResult<DataError> = Result.Success(Unit)
        },
        availabilityRepository = object : AvailabilityRepository {
            override fun observeAvailability(date: LocalDate) = MutableStateFlow(availability[date])
            override suspend fun refreshAvailability(date: LocalDate): EmptyResult<DataError> = Result.Success(Unit)
        },
    )

    private fun court(opening: Int, closing: Int) = Court(
        id = "c",
        name = "Court",
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
