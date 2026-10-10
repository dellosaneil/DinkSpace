package com.thelazybattley.dinkspace.core.data

import com.thelazybattley.dinkspace.core.domain.DateProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class SystemDateProvider : DateProvider {
    override fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
}
