package com.thelazybattley.dinkspace.feature.courts.data.remote

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result

interface CourtRemoteDataSource {
    suspend fun fetchCourts(): Result<List<CourtDto>, DataError.Remote>
}
