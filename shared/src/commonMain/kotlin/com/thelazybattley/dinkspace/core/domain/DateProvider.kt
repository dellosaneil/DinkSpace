package com.thelazybattley.dinkspace.core.domain

import kotlinx.datetime.LocalDate

/** Source of "today", injectable so date logic can be tested. */
fun interface DateProvider {
    fun today(): LocalDate
}
