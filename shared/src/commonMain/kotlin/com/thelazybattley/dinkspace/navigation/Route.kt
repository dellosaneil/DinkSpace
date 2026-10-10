package com.thelazybattley.dinkspace.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Courts : Route

    @Serializable
    data class CourtDetail(val courtId: String) : Route

    @Serializable
    data object OpenPlay : Route

    @Serializable
    data object Partners : Route

    @Serializable
    data object Record : Route
}
