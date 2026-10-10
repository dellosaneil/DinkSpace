package com.thelazybattley.dinkspace.feature.courts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courts")
data class CourtEntity(
    @PrimaryKey val id: String,
    val name: String,
    val area: String,
    val address: String,
    val courtCount: Int,
    val setting: String,
    val openingHour: Int,
    val closingHour: Int,
    val phoneNumber: String,
    val messengerUrl: String?,
    val logoUrl: String?,
    val photoUrls: List<String>,
    val standardRate: Double?,
    val peakRate: Double?,
    val peakStartHour: Int?,
    val peakEndHour: Int?,
)
