package com.thelazybattley.dinkspace.feature.courts.presentation.list

import kotlinx.datetime.LocalDate

sealed interface CourtsAction {
    data class OnFilterModeChange(val mode: CourtFilterMode) : CourtsAction
    data class OnDateSelect(val date: LocalDate) : CourtsAction
    data class OnHourClick(val hour: Int) : CourtsAction
    data class OnQueryChange(val query: String) : CourtsAction
    data class OnCourtClick(val courtId: String) : CourtsAction
    data object OnRefresh : CourtsAction
    data object OnErrorShown : CourtsAction
}
