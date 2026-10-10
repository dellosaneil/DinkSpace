package com.thelazybattley.dinkspace.feature.courts.data.mapper

import com.thelazybattley.dinkspace.feature.courts.data.remote.AvailabilityDto
import com.thelazybattley.dinkspace.feature.courts.domain.model.DailyAvailability
import kotlinx.datetime.LocalDate

fun AvailabilityDto.toDomain(): DailyAvailability = DailyAvailability(
    courtId = courtId,
    date = LocalDate.parse(date),
    freeCourtsByHour = freeCourtsByHour.mapNotNull { (hour, free) ->
        hour.toIntOrNull()?.let { it to free }
    }.toMap(),
)
