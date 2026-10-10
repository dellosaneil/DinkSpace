package com.thelazybattley.dinkspace.feature.courts.data.remote

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result
import kotlinx.coroutines.delay

/** Serves [DummyCourts] with a short delay so loading and refresh states are visible. */
class FakeCourtRemoteDataSource : CourtRemoteDataSource {

    override suspend fun fetchCourts(): Result<List<CourtDto>, DataError.Remote> {
        delay(FAKE_LATENCY_MS)
        return Result.Success(DummyCourts.all)
    }

    private companion object {
        const val FAKE_LATENCY_MS = 800L
    }
}
