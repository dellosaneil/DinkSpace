package com.thelazybattley.dinkspace.feature.courts.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.DateProvider
import com.thelazybattley.dinkspace.core.domain.onFailure
import com.thelazybattley.dinkspace.core.presentation.toStringResource
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtAvailabilityUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshAvailabilityUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshCourtsUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.SearchCourtsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class CourtsViewModel(
    private val searchCourts: SearchCourtsUseCase,
    private val refreshCourts: RefreshCourtsUseCase,
    private val observeCourtAvailability: ObserveCourtAvailabilityUseCase,
    private val refreshAvailability: RefreshAvailabilityUseCase,
    dateProvider: DateProvider,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(initialState(dateProvider.today()))
    val state: StateFlow<CourtsState> = _state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                observeCourts()
                observeAvailability()
                viewModelScope.launch { refreshCourts().onFailure(::showError) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: CourtsAction) {
        when (action) {
            is CourtsAction.OnFilterModeChange -> _state.update { it.copy(filterMode = action.mode) }
            is CourtsAction.OnDateSelect -> _state.update { it.copy(selectedDate = action.date) }
            is CourtsAction.OnHourClick -> _state.update {
                it.copy(timeSelection = it.timeSelection.onHourClick(action.hour))
            }
            is CourtsAction.OnQueryChange -> _state.update { it.copy(query = action.query) }
            CourtsAction.OnRefresh -> refreshAll()
            CourtsAction.OnErrorShown -> _state.update { it.copy(error = null) }
            // Navigation is handled by the screen's root composable.
            is CourtsAction.OnCourtClick -> Unit
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCourts() {
        _state
            .map { it.query }
            .distinctUntilChanged()
            .flatMapLatest { query -> searchCourts(query) }
            .onEach { courts -> _state.update { it.copy(courts = courts, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAvailability() {
        _state
            .map { it.selectedDate to it.timeSelection.range }
            .distinctUntilChanged()
            .flatMapLatest { (date, range) -> observeCourtAvailability(date, range) }
            .onEach { availability ->
                _state.update {
                    it.copy(availability = availability.orEmpty(), isLoadingAvailability = availability == null)
                }
            }
            .launchIn(viewModelScope)

        // Refetch whenever the day changes; a cached day still shows instantly meanwhile.
        _state
            .map { it.selectedDate }
            .distinctUntilChanged()
            .onEach { date -> viewModelScope.launch { refreshAvailability(date).onFailure(::showError) } }
            .launchIn(viewModelScope)
    }

    private fun refreshAll() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            coroutineScope {
                launch { refreshCourts().onFailure(::showError) }
                launch { refreshAvailability(_state.value.selectedDate).onFailure(::showError) }
            }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun showError(error: DataError) {
        _state.update { it.copy(error = error.toStringResource()) }
    }

    private companion object {
        const val DAYS_SHOWN = 7

        fun initialState(today: LocalDate) = CourtsState(
            dates = List(DAYS_SHOWN) { today.plus(it, DateTimeUnit.DAY) },
            selectedDate = today,
        )
    }
}
