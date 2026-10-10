package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thelazybattley.dinkspace.core.domain.DateProvider
import com.thelazybattley.dinkspace.core.domain.onFailure
import com.thelazybattley.dinkspace.core.presentation.toStringResource
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtScheduleUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshAvailabilityUseCase
import com.thelazybattley.dinkspace.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class CourtDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val observeCourtSchedule: ObserveCourtScheduleUseCase,
    private val refreshAvailability: RefreshAvailabilityUseCase,
    dateProvider: DateProvider,
) : ViewModel() {

    private val courtId = savedStateHandle.toRoute<Route.CourtDetail>().courtId
    private var hasLoadedInitialData = false
    private val loadedDates = mutableSetOf<LocalDate>()

    private val _state = MutableStateFlow(initialState(dateProvider.today()))
    val state: StateFlow<CourtDetailState> = _state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                observeSchedule()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: CourtDetailAction) {
        when (action) {
            is CourtDetailAction.OnDateSelect -> _state.update { it.copy(selectedDate = action.date) }
            CourtDetailAction.OnErrorShown -> _state.update { it.copy(error = null) }
            // Navigation and opening links are handled by the screen's root composable.
            CourtDetailAction.OnBackClick,
            is CourtDetailAction.OnCallClick,
            is CourtDetailAction.OnMessengerClick,
            is CourtDetailAction.OnOpenMapsClick -> Unit
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSchedule() {
        _state
            .map { it.selectedDate }
            .distinctUntilChanged()
            .flatMapLatest { date -> observeCourtSchedule(courtId, date) }
            .onEach { schedule ->
                _state.update { it.copy(court = schedule.court, schedule = schedule.slots, isLoading = false) }
            }
            .launchIn(viewModelScope)

        // Load each day the opening hours touch (two when the venue closes after midnight).
        _state
            .mapNotNull { state -> state.court?.openRange?.datesFrom(state.selectedDate) }
            .distinctUntilChanged()
            .onEach { dates -> dates.filter { it !in loadedDates }.forEach(::loadAvailability) }
            .launchIn(viewModelScope)
    }

    private fun loadAvailability(date: LocalDate) {
        loadedDates += date
        viewModelScope.launch {
            refreshAvailability(date).onFailure { error ->
                loadedDates -= date
                _state.update { it.copy(error = error.toStringResource()) }
            }
        }
    }

    private companion object {
        const val DAYS_SHOWN = 7

        fun initialState(today: LocalDate) = CourtDetailState(
            dates = List(DAYS_SHOWN) { today.plus(it, DateTimeUnit.DAY) },
            selectedDate = today,
        )
    }
}
