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
    /** Null when the venue hasn't shared its prices. */
    val rates: CourtRates?,
) {
    /**
     * Whether the venue is open during the hour starting at [hourOfDay] (0–23).
     * [closingHour] may exceed 24 for venues open past midnight (26 = 2 AM).
     */
    fun isOpenAt(hourOfDay: Int): Boolean =
        hourOfDay in openingHour until closingHour || hourOfDay + 24 < closingHour

    /** Opening hours as a range starting on the opening day, or null if the hours are invalid. */
    val openRange: TimeRange?
        get() = if (openingHour in 0..23 && closingHour in (openingHour + 1)..(openingHour + 24)) {
            TimeRange(openingHour, closingHour)
        } else {
            null
        }
}

enum class CourtSetting { INDOOR, OUTDOOR }

/** The phone number is required for every venue; Messenger is optional. */
data class CourtContact(
    val phoneNumber: String,
    val messengerUrl: String?,
)

/**
 * Price per court per hour. A venue may charge [peakRate] from [peakStartHour] (inclusive)
 * to [peakEndHour] (exclusive); every other hour uses [standardRate].
 */
data class CourtRates(
    val standardRate: Double,
    val peakRate: Double? = null,
    val peakStartHour: Int? = null,
    val peakEndHour: Int? = null,
) {
    fun rateAt(hourOfDay: Int): Double {
        if (peakRate == null || peakStartHour == null || peakEndHour == null) return standardRate
        return if (hourOfDay in peakStartHour until peakEndHour) peakRate else standardRate
    }
}
