package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtAvailability
import com.thelazybattley.dinkspace.feature.courts.presentation.TimeRangeSelection
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

data class CourtsState(
    val dates: List<LocalDate>,
    val selectedDate: LocalDate,
    val timeSelection: TimeRangeSelection = TimeRangeSelection(),
    val query: String = "",
    val availability: List<CourtAvailability> = emptyList(),
    val isLoadingAvailability: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: StringResource? = null,
)
