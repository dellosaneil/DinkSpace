package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository

class RefreshCourtsUseCase(private val repository: CourtRepository) {
    suspend operator fun invoke(): EmptyResult<DataError> = repository.refreshCourts()
}
