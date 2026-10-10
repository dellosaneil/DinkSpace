package com.thelazybattley.dinkspace.feature.courts.data.mapper

import com.thelazybattley.dinkspace.feature.courts.data.local.CourtEntity
import com.thelazybattley.dinkspace.feature.courts.data.remote.CourtDto
import com.thelazybattley.dinkspace.feature.courts.domain.model.Court
import com.thelazybattley.dinkspace.feature.courts.domain.model.CourtContact
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
    email = email?.takeIf { it.isNotBlank() },
    logoUrl = logoUrl?.takeIf { it.isNotBlank() },
    photoUrls = photoUrls,
    hourlyRate = hourlyRate,
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
        email = email,
    ),
    logoUrl = logoUrl,
    photoUrls = photoUrls,
    hourlyRate = hourlyRate,
)
