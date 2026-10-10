package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.HourSlot
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

data class CourtDetailState(
    val dates: List<LocalDate>,
    val selectedDate: LocalDate,
    val court: Court? = null,
    val isLoading: Boolean = true,
    /** Null while the selected day's availability is loading. */
    val schedule: List<HourSlot>? = null,
    val error: StringResource? = null,
)
