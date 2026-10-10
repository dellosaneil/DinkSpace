package com.thelazybattley.dinkspace.feature.courts.data.remote

import kotlinx.serialization.Serializable

/**
 * Shape of a document in the Firestore `availability` collection: one per court per day.
 * [date] is ISO-8601 (2026-10-10) and [freeCourtsByHour] is keyed by start hour ("16").
 */
@Serializable
data class AvailabilityDto(
    val courtId: String = "",
    val date: String = "",
    val freeCourtsByHour: Map<String, Int> = emptyMap(),
)
