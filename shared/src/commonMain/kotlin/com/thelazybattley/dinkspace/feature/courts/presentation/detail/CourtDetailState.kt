package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import com.thelazybattley.dinkspace.feature.courts.presentation.TimeRangeSelection
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

data class CourtDetailState(
    val dates: List<LocalDate>,
    val selectedDate: LocalDate,
    val timeSelection: TimeRangeSelection = TimeRangeSelection(),
    val court: Court? = null,
    val isLoading: Boolean = true,
    /** Slots in the selected range; null while their availability is loading. */
    val schedule: List<HourSlot>? = null,
    val error: StringResource? = null,
)
