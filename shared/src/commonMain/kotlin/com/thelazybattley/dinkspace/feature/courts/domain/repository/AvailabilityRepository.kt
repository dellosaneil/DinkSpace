package com.thelazybattley.dinkspace.feature.courts.domain.repository

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface AvailabilityRepository {
    /** Emits null until the date has been loaded with [refreshAvailability]. */
    fun observeAvailability(date: LocalDate): Flow<List<DailyAvailability>?>

    suspend fun refreshAvailability(date: LocalDate): EmptyResult<DataError>
}
