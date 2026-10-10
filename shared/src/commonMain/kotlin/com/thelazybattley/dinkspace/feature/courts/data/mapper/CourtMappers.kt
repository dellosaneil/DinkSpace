package com.thelazybattley.dinkspace.feature.courts.data.mapper

import com.thelazybattley.dinkspace.feature.courts.data.local.CourtEntity
import com.thelazybattley.dinkspace.feature.courts.data.remote.CourtDto
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtRates
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtSetting

fun CourtDto.toEntity(): CourtEntity = CourtEntity(
    id = id,
    name = name,
    area = area,
    address = address,
    courtCount = courtCount,
    setting = setting,
    openingHour = openingHour,
    closingHour = closingHour,
    phoneNumber = phoneNumber,
    messengerUrl = messengerUrl?.takeIf { it.isNotBlank() },
    logoUrl = logoUrl?.takeIf { it.isNotBlank() },
    photoUrls = photoUrls,
    standardRate = standardRate,
    peakRate = peakRate,
    peakStartHour = peakStartHour,
    peakEndHour = peakEndHour,
)

fun CourtEntity.toDomain(): Court = Court(
    id = id,
    name = name,
    area = area,
    address = address,
    courtCount = courtCount,
    setting = if (setting.equals("indoor", ignoreCase = true)) CourtSetting.INDOOR else CourtSetting.OUTDOOR,
    openingHour = openingHour,
    closingHour = closingHour,
    contact = CourtContact(
        phoneNumber = phoneNumber,
        messengerUrl = messengerUrl,
    ),
    logoUrl = logoUrl,
    photoUrls = photoUrls,
    rates = standardRate?.let { standard ->
        CourtRates(
            standardRate = standard,
            peakRate = peakRate,
            peakStartHour = peakStartHour,
            peakEndHour = peakEndHour,
        )
    },
)
