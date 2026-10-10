package com.thelazybattley.dinkspace.feature.courts.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thelazybattley.dinkspace.core.domain.onFailure
import com.thelazybattley.dinkspace.core.presentation.toStringResource
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.RefreshCourtsUseCase
import com.thelazybattley.dinkspace.feature.courts.domain.usecase.SearchCourtsUseCase
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

class CourtsViewModel(
    private val searchCourts: SearchCourtsUseCase,
    private val refreshCourts: RefreshCourtsUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CourtsState())
    val state: StateFlow<CourtsState> = _state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                observeCourts()
                refresh()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: CourtsAction) {
        when (action) {
            is CourtsAction.OnQueryChange -> _state.update { it.copy(query = action.query) }
            CourtsAction.OnRefresh -> refresh()
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

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            refreshCourts().onFailure { error ->
                _state.update { it.copy(error = error.toStringResource()) }
            }
            _state.update { it.copy(isRefreshing = false, isLoading = false) }
        }
    }
}
