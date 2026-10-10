package com.thelazybattley.dinkspace.feature.courts.data.remote

import kotlinx.serialization.Serializable

/** Shape of a document in the Firestore `courts` collection. The document id is filled in separately. */
@Serializable
data class CourtDto(
    val id: String = "",
    val name: String = "",
    val area: String = "",
    val address: String = "",
    val courtCount: Int = 0,
    val setting: String = "",
    val openingHour: Int = 0,
    val closingHour: Int = 0,
    val phoneNumber: String = "",
    val messengerUrl: String? = null,
    val logoUrl: String? = null,
    val photoUrls: List<String> = emptyList(),
    /** Price per court per hour; leave out when the venue hasn't shared prices. */
    val standardRate: Double? = null,
    /** Optional peak price, charged from peakStartHour (inclusive) to peakEndHour (exclusive). */
    val peakRate: Double? = null,
    val peakStartHour: Int? = null,
    val peakEndHour: Int? = null,
)
