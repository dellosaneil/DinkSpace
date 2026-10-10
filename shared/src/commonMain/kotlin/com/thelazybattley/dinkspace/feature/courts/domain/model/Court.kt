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
) {
    /**
     * Whether the venue is open during the hour starting at [hourOfDay] (0–23).
     * [closingHour] may exceed 24 for venues open past midnight (26 = 2 AM).
     */
    fun isOpenAt(hourOfDay: Int): Boolean =
        hourOfDay in openingHour until closingHour || hourOfDay + 24 < closingHour
}

enum class CourtSetting { INDOOR, OUTDOOR }

/** The phone number is required for every venue; Messenger and email are optional. */
data class CourtContact(
    val phoneNumber: String,
    val messengerUrl: String?,
    val email: String?,
)
