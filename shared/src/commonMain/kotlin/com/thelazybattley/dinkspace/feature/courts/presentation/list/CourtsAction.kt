package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.presentation.TimeField
import kotlinx.datetime.LocalDate

sealed interface CourtsAction {
    data class OnDateSelect(val date: LocalDate) : CourtsAction
    data class OnTimeFieldClick(val field: TimeField) : CourtsAction
    data class OnTimeHourPick(val hour: Int) : CourtsAction
    data object OnTimePanelDismiss : CourtsAction
    data class OnQueryChange(val query: String) : CourtsAction
    data class OnCourtClick(val courtId: String) : CourtsAction
    data object OnRefresh : CourtsAction
    data object OnErrorShown : CourtsAction
}
