package com.thelazybattley.dinkspace.feature.courts.presentation

import com.thelazybattley.dinkspace.feature.courts.domain.model.TimeRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimeRangeSelectionTest {

    private val fourToSix = TimeRangeSelection(TimeRange(16, 18))

    @Test
    fun toggleOpensAndClosesAField() {
        val open = fourToSix.toggle(TimeField.END)

        assertEquals(TimeField.END, open.openField)
        assertNull(open.toggle(TimeField.END).openField)
        assertEquals(TimeField.START, open.toggle(TimeField.START).openField)
    }

    @Test
    fun pickingAnEndMovesOnlyTheEndAndClosesThePanel() {
        val selection = fourToSix.toggle(TimeField.END).pick(20)

        assertEquals(TimeRange(16, 20), selection.range)
        assertNull(selection.openField)
    }

    @Test
    fun repeatedEndPicksNeverResetTheStart() {
        val selection = fourToSix.toggle(TimeField.END).pick(19).toggle(TimeField.END).pick(21)

        assertEquals(TimeRange(16, 21), selection.range)
    }

    @Test
    fun endBeforeTheStartEndsTheNextDay() {
        val selection = TimeRangeSelection(TimeRange(12, 14)).toggle(TimeField.END).pick(1)

        assertEquals(TimeRange(12, 25), selection.range)
        assertTrue(selection.range.crossesMidnight)
    }

    @Test
    fun endAtMidnightStaysOnTheSameDay() {
        val selection = TimeRangeSelection(TimeRange(22, 23)).toggle(TimeField.END).pick(0)

        assertEquals(TimeRange(22, 24), selection.range)
        assertFalse(selection.range.crossesMidnight)
    }

    @Test
    fun pickingAStartKeepsTheChosenEnd() {
        assertEquals(TimeRange(9, 18), fourToSix.toggle(TimeField.START).pick(9).range)
        // The end hour now comes before the start, so it moves to the next day.
        assertEquals(TimeRange(20, 42), fourToSix.toggle(TimeField.START).pick(20).range)
        // Same hour as the end: fall back to a one-hour range.
        assertEquals(TimeRange(18, 19), fourToSix.toggle(TimeField.START).pick(18).range)
    }

    @Test
    fun pickWithNoOpenFieldDoesNothing() {
        assertEquals(fourToSix, fourToSix.pick(9))
    }

    @Test
    fun marksSelectedAndNextDayHours() {
        val end = fourToSix.toggle(TimeField.END)

        assertTrue(end.isSelected(18))
        assertTrue(end.isNextDay(16))
        assertTrue(end.isNextDay(3))
        assertFalse(end.isNextDay(17))
        assertTrue(fourToSix.toggle(TimeField.START).isSelected(16))
        assertFalse(fourToSix.toggle(TimeField.START).isNextDay(3))
    }
}
