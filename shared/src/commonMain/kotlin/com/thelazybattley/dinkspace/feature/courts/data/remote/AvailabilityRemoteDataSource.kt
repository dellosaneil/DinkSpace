package com.thelazybattley.dinkspace.feature.courts.data.remote

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result
import kotlinx.datetime.LocalDate

interface AvailabilityRemoteDataSource {
    suspend fun fetchAvailability(date: LocalDate): Result<List<AvailabilityDto>, DataError.Remote>
}
