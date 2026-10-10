package com.thelazybattley.dinkspace.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Courts : Route

    /**
     * [date] (ISO-8601) and the hour range carry the Courts screen's filter so the details
     * screen opens on the same day and hours; all three are optional.
     */
    @Serializable
    data class CourtDetail(
        val courtId: String,
        val date: String? = null,
        val startHour: Int? = null,
        val endHour: Int? = null,
    ) : Route

    @Serializable
    data object OpenPlay : Route

    @Serializable
    data object Partners : Route

    @Serializable
    data object Record : Route
}
