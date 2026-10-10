package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thelazybattley.dinkspace.core.domain.DateProvider
import com.thelazybattley.dinkspace.core.domain.onFailure
import com.thelazybattley.dinkspace.core.presentation.toStringResource
import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.ObserveCourtScheduleUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshAvailabilityUseCase
import com.thelazybattley.dinkspace.feature.courts.presentation.TimeRangeSelection
import com.thelazybattley.dinkspace.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

class CourtDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val observeCourtSchedule: ObserveCourtScheduleUseCase,
    private val refreshAvailability: RefreshAvailabilityUseCase,
    dateProvider: DateProvider,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.CourtDetail>()
    private val courtId = route.courtId
    private var hasLoadedInitialData = false
    private val loadedDates = mutableSetOf<LocalDate>()

    private val _state = MutableStateFlow(initialState(dateProvider.today(), route))
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
            is CourtDetailAction.OnHourClick -> _state.update {
                it.copy(timeSelection = it.timeSelection.onHourClick(action.hour))
            }
            CourtDetailAction.OnShowAllOpenHours -> _state.update { state ->
                val openRange = state.court?.openRange ?: return@update state
                state.copy(timeSelection = TimeRangeSelection(openRange))
            }
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
            .map { it.selectedDate to it.timeSelection.range }
            .distinctUntilChanged()
            .flatMapLatest { (date, range) -> observeCourtSchedule(courtId, date, range) }
            .onEach { schedule ->
                _state.update { it.copy(court = schedule.court, schedule = schedule.slots, isLoading = false) }
            }
            .launchIn(viewModelScope)

        // Load each day the selected range touches (two when it runs past midnight).
        _state
            .map { it.timeSelection.range.datesFrom(it.selectedDate) }
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

        /** Opens on the day and hours the Courts screen was filtered to, when they were passed. */
        fun initialState(today: LocalDate, route: Route.CourtDetail): CourtDetailState {
            val dates = List(DAYS_SHOWN) { today.plus(it, DateTimeUnit.DAY) }
            val date = route.date?.let { runCatching { LocalDate.parse(it) }.getOrNull() }?.takeIf { it in dates }
            val start = route.startHour
            val end = route.endHour
            val range = if (start != null && end != null) runCatching { TimeRange(start, end) }.getOrNull() else null
            return CourtDetailState(
                dates = dates,
                selectedDate = date ?: today,
                timeSelection = range?.let { TimeRangeSelection(it) } ?: TimeRangeSelection(),
            )
        }
    }
}
