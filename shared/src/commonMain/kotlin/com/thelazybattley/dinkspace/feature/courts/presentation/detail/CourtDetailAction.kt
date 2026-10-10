package com.thelazybattley.dinkspace.feature.courts.presentation.detail

import kotlinx.datetime.LocalDate

sealed interface CourtDetailAction {
    data object OnBackClick : CourtDetailAction
    data class OnDateSelect(val date: LocalDate) : CourtDetailAction
    data class OnCallClick(val phoneNumber: String) : CourtDetailAction
    data class OnMessengerClick(val url: String) : CourtDetailAction
    data class OnOpenMapsClick(val url: String) : CourtDetailAction
    data object OnErrorShown : CourtDetailAction
}
