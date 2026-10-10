package com.thelazybattley.dinkspace.feature.courts.domain.usecase

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.repository.CourtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Matches the query against a court's name or area; a blank query returns every court. */
class SearchCourtsUseCase(private val repository: CourtRepository) {
    operator fun invoke(query: String): Flow<List<Court>> {
        val term = query.trim()
        return repository.observeCourts().map { courts ->
            if (term.isEmpty()) {
                courts
            } else {
                courts.filter { it.name.contains(term, ignoreCase = true) || it.area.contains(term, ignoreCase = true) }
            }
        }
    }
}
