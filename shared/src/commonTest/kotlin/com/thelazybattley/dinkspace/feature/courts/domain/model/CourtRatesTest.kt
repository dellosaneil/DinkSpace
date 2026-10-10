package com.thelazybattley.dinkspace.feature.courts.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CourtRatesTest {

    @Test
    fun flatRateAppliesToEveryHour() {
        val rates = CourtRates(standardRate = 10.0)

        assertEquals(10.0, rates.rateAt(6))
        assertEquals(10.0, rates.rateAt(20))
    }

    @Test
    fun peakRateAppliesFromStartUntilEnd() {
        val rates = CourtRates(standardRate = 10.0, peakRate = 14.0, peakStartHour = 17, peakEndHour = 22)

        assertEquals(10.0, rates.rateAt(16))
        assertEquals(14.0, rates.rateAt(17))
        assertEquals(14.0, rates.rateAt(21))
        assertEquals(10.0, rates.rateAt(22))
    }

    @Test
    fun incompletePeakWindowFallsBackToStandard() {
        val rates = CourtRates(standardRate = 10.0, peakRate = 14.0, peakStartHour = 17)

        assertEquals(10.0, rates.rateAt(18))
    }
}
