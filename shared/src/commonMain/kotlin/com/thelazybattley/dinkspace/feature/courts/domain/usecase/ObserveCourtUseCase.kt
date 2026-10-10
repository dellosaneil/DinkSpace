package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.flow.Flow

class ObserveCourtUseCase(private val repository: CourtRepository) {
    operator fun invoke(id: String): Flow<Court?> = repository.observeCourt(id)
}
