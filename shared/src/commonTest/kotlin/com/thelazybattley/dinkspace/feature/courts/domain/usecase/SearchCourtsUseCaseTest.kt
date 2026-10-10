package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.core.domain.Result
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchCourtsUseCaseTest {

    private val riverside = court(id = "1", name = "Riverside Park Courts", area = "Westside")
    private val northgate = court(id = "2", name = "Northgate Rec Center", area = "Northgate")
    private val searchCourts = SearchCourtsUseCase(FakeCourtRepository(listOf(riverside, northgate)))

    @Test
    fun blankQueryReturnsAllCourts() = runTest {
        assertEquals(listOf(riverside, northgate), searchCourts("  ").first())
    }

    @Test
    fun matchesNameIgnoringCase() = runTest {
        assertEquals(listOf(riverside), searchCourts("riverSIDE").first())
    }

    @Test
    fun matchesArea() = runTest {
        assertEquals(listOf(riverside), searchCourts("westside").first())
    }

    private class FakeCourtRepository(private val courts: List<Court>) : CourtRepository {
        override fun observeCourts(): Flow<List<Court>> = flowOf(courts)
        override fun observeCourt(id: String): Flow<Court?> = flowOf(courts.find { it.id == id })
        override suspend fun refreshCourts(): EmptyResult<DataError> = Result.Success(Unit)
    }

    private fun court(id: String, name: String, area: String) = Court(
        id = id,
        name = name,
        area = area,
        address = "",
        courtCount = 4,
        setting = CourtSetting.OUTDOOR,
        openingHour = 6,
        closingHour = 22,
        contact = CourtContact(phoneNumber = "000", messengerUrl = null),
        logoUrl = null,
        photoUrls = emptyList(),
        rates = null,
    )
}
