package com.thelazybattley.dinkspace.feature.courts.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

class CourtFormattingTest {

    @Test
    fun encodesSpacesCommasAndNonAscii() {
        assertEquals("Riverside%20Park%2C%20120%20River%20Rd", encodeQueryParameter("Riverside Park, 120 River Rd"))
        assertEquals("Caf%C3%A9", encodeQueryParameter("Café"))
    }

    @Test
    fun keepsUnreservedCharacters() {
        assertEquals("a-b_c.d~e9", encodeQueryParameter("a-b_c.d~e9"))
    }

    @Test
    fun formatsWholeAndFractionalRates() {
        assertEquals("12", formatRate(12.0))
        assertEquals("12.50", formatRate(12.5))
    }
}
