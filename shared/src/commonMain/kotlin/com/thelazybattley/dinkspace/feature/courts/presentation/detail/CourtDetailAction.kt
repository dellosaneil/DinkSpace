package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import com.thelazybattley.dinkspace.feature.courts.presentation.TimeField
import kotlinx.datetime.LocalDate

sealed interface CourtDetailAction {
    data object OnBackClick : CourtDetailAction
    data class OnDateSelect(val date: LocalDate) : CourtDetailAction
    data class OnTimeFieldClick(val field: TimeField) : CourtDetailAction
    data class OnTimeHourPick(val hour: Int) : CourtDetailAction
    data object OnTimePanelDismiss : CourtDetailAction
    data object OnShowAllOpenHours : CourtDetailAction
    data class OnCallClick(val phoneNumber: String) : CourtDetailAction
    data class OnMessengerClick(val url: String) : CourtDetailAction
    data class OnOpenMapsClick(val url: String) : CourtDetailAction
    data object OnErrorShown : CourtDetailAction
}
