package com.thelazybattley.dinkspace.feature.courts.presentation.detail

sealed interface CourtDetailAction {
    data object OnBackClick : CourtDetailAction
    data class OnCallClick(val phoneNumber: String) : CourtDetailAction
    data class OnMessengerClick(val url: String) : CourtDetailAction
    data class OnEmailClick(val email: String) : CourtDetailAction
}
