package com.thelazybattley.dinkspace.feature.courts.data.repository

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.core.domain.Result
import com.thelazybattley.dinkspace.feature.courts.data.mapper.toDomain
import com.thelazybattley.dinkspace.feature.courts.data.remote.AvailabilityRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

/**
 * Availability changes minute to minute, so it is kept in memory for the session
 * instead of Room; each refresh replaces that day's snapshot.
 */
class InMemoryAvailabilityRepository(
    private val remoteDataSource: AvailabilityRemoteDataSource,
) : AvailabilityRepository {

    private val cache = MutableStateFlow<Map<LocalDate, List<DailyAvailability>>>(emptyMap())

    override fun observeAvailability(date: LocalDate): Flow<List<DailyAvailability>?> =
        cache.map { it[date] }.distinctUntilChanged()

    override suspend fun refreshAvailability(date: LocalDate): EmptyResult<DataError> =
        when (val result = remoteDataSource.fetchAvailability(date)) {
            is Result.Failure -> result
            is Result.Success -> {
                val availability = result.data.map { it.toDomain() }
                cache.update { it + (date to availability) }
                Result.Success(Unit)
            }
        }
}
