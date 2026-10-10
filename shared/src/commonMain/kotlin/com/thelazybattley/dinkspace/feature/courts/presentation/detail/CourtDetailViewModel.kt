package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtUseCase
import com.thelazybattley.dinkspace.navigation.Route
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CourtDetailViewModel(
    savedStateHandle: SavedStateHandle,
    observeCourt: ObserveCourtUseCase,
) : ViewModel() {

    private val courtId = savedStateHandle.toRoute<Route.CourtDetail>().courtId

    val state: StateFlow<CourtDetailState> = observeCourt(courtId)
        .map { court -> CourtDetailState(court = court, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CourtDetailState(),
        )
}
