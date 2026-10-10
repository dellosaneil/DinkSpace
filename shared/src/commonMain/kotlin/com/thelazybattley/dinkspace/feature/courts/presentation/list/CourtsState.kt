package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtAvailability
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

enum class CourtFilterMode { DATE_TIME, NAME }

data class CourtsState(
    val dates: List<LocalDate>,
    val selectedDate: LocalDate,
    val filterMode: CourtFilterMode = CourtFilterMode.DATE_TIME,
    val timeSelection: TimeRangeSelection = TimeRangeSelection(),
    val query: String = "",
    val courts: List<Court> = emptyList(),
    val availability: List<CourtAvailability> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingAvailability: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: StringResource? = null,
)
