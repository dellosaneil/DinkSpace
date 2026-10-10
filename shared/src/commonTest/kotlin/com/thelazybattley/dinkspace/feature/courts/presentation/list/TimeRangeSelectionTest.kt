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
    fun secondTapBeforeTheStartEndsTheNextDay() {
        val selection = TimeRangeSelection().onHourClick(12).onHourClick(1)

        assertEquals(TimeRange(12, 25), selection.range)
        assertTrue(selection.range.crossesMidnight)
        assertFalse(selection.isPickingEnd)
    }

    @Test
    fun endingAtMidnightStaysOnTheSameDay() {
        val selection = TimeRangeSelection().onHourClick(22).onHourClick(0)

        assertEquals(TimeRange(22, 24), selection.range)
        assertFalse(selection.range.crossesMidnight)
    }

    @Test
    fun tappingTheStartAgainRestartsThere() {
        val selection = TimeRangeSelection().onHourClick(9).onHourClick(9)

        assertEquals(TimeRange(9, 10), selection.range)
        assertTrue(selection.isPickingEnd)
    }

    @Test
    fun aTapAfterACompleteRangeStartsANewOne() {
        val selection = TimeRangeSelection().onHourClick(9).onHourClick(12).onHourClick(3)

        assertEquals(TimeRange(3, 4), selection.range)
        assertTrue(selection.isPickingEnd)
    }

    @Test
    fun highlightsWrapPastMidnight() {
        val selection = TimeRangeSelection(TimeRange(22, 26))

        assertTrue(selection.isEdge(22))
        assertTrue(selection.isEdge(2))
        assertTrue(selection.isBetween(23))
        assertTrue(selection.isBetween(0))
        assertTrue(selection.isBetween(1))
        assertFalse(selection.isBetween(2))
        assertFalse(selection.isBetween(12))
    }
}
