package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.EmptyResult
import com.thelazybattley.dinkspace.feature.courts.domain.repository.AvailabilityRepository
import kotlinx.datetime.LocalDate

class RefreshAvailabilityUseCase(private val repository: AvailabilityRepository) {
    suspend operator fun invoke(date: LocalDate): EmptyResult<DataError> = repository.refreshAvailability(date)
}
