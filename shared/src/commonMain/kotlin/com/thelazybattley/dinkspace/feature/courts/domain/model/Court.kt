package com.thelazybattley.dinkspace.feature.courts.domain.model

data class Court(
    val id: String,
    val name: String,
    val area: String,
    val address: String,
    val courtCount: Int,
    val setting: CourtSetting,
    val openingHour: Int,
    val closingHour: Int,
    val contact: CourtContact,
    val logoUrl: String?,
    val photoUrls: List<String>,
    val hourlyRate: Double?,
)

enum class CourtSetting { INDOOR, OUTDOOR }

/** The phone number is required for every venue; Messenger and email are optional. */
data class CourtContact(
    val phoneNumber: String,
    val messengerUrl: String?,
    val email: String?,
)
