package com.thelazybattley.dinkspace.feature.courts.domain.repository

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import kotlinx.coroutines.flow.Flow

interface CourtRepository {
    /** Courts from the local cache. Call [refreshCourts] to sync with the backend. */
    fun observeCourts(): Flow<List<Court>>

    fun observeCourt(id: String): Flow<Court?>

    suspend fun refreshCourts(): EmptyResult<DataError>
}
