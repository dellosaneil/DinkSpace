package com.thelazybattley.dinkspace.feature.courts.data.repository

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.core.domain.Result
import com.thelazybattley.dinkspace.feature.courts.data.local.CourtDao
import com.thelazybattley.dinkspace.feature.courts.data.mapper.toDomain
import com.thelazybattley.dinkspace.feature.courts.data.mapper.toEntity
import com.thelazybattley.dinkspace.feature.courts.data.remote.CourtRemoteDataSource
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room is the single source of truth; Firestore only feeds it on refresh. */
class OfflineFirstCourtRepository(
    private val courtDao: CourtDao,
    private val remoteDataSource: CourtRemoteDataSource,
) : CourtRepository {

    override fun observeCourts(): Flow<List<Court>> =
        courtDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeCourt(id: String): Flow<Court?> =
        courtDao.observeById(id).map { it?.toDomain() }

    override suspend fun refreshCourts(): EmptyResult<DataError> =
        when (val result = remoteDataSource.fetchCourts()) {
            is Result.Failure -> result
            is Result.Success -> {
                courtDao.replaceAll(result.data.map { it.toEntity() })
                Result.Success(Unit)
            }
        }
}
