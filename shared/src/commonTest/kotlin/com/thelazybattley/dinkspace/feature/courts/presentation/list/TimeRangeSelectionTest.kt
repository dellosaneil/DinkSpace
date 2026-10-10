package com.thelazybattley.dinkspace.feature.courts.presentation.list

import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeRangeSelectionTest {

    @Test
    fun firstTapStartsAOneHourRangeAndWaitsForTheEnd() {
        val selection = TimeRangeSelection().onHourClick(9)

        assertEquals(TimeRange(9, 10), selection.range)
        assertTrue(selection.isPickingEnd)
    }

    @Test
    fun secondTapAfterTheStartSetsTheEnd() {
        val selection = TimeRangeSelection().onHourClick(9).onHourClick(12)

        assertEquals(TimeRange(9, 12), selection.range)
        assertFalse(selection.isPickingEnd)
    }

    @Test
    fun secondTapAtOrBeforeTheStartRestartsFromThatHour() {
        val selection = TimeRangeSelection().onHourClick(9).onHourClick(7)

        assertEquals(TimeRange(7, 8), selection.range)
        assertTrue(selection.isPickingEnd)
    }

    @Test
    fun tappingTheLastHourAsStartKeepsAOneHourRange() {
        val selection = TimeRangeSelection().onHourClick(TimeRangeSelection.LAST_HOUR)

        assertEquals(TimeRange(TimeRangeSelection.LAST_HOUR - 1, TimeRangeSelection.LAST_HOUR), selection.range)
    }

    @Test
    fun highlightsEdgesAndHoursBetween() {
        val selection = TimeRangeSelection(TimeRange(16, 19))

        assertTrue(selection.isEdge(16))
        assertTrue(selection.isEdge(19))
        assertTrue(selection.isBetween(17))
        assertFalse(selection.isBetween(19))
        assertFalse(selection.isEdge(20))
    }
}
