package com.sepideh.lilo.core.presentation.format

import kotlinx.datetime.TimeZone
import kotlin.test.*

class CreationDateTest {
    @Test fun hidesUnknownOrUnsavedDates() {
        assertNull(creationDateLabel(0, false, TimeZone.UTC))
        assertNull(creationDateLabel(-1, true, TimeZone.UTC))
    }
    @Test fun respectsDisplayTimeZone() {
        val timestamp = 1767225600000L
        assertEquals("2026-01-01", creationDateLabel(timestamp, false, TimeZone.UTC))
        assertEquals("2025-12-31", creationDateLabel(timestamp, false, TimeZone.of("America/Los_Angeles")))
    }
    @Test fun persianDateUsesPersianCalendarAndDigits() {
        val label = assertNotNull(creationDateLabel(1767225600000L, true, TimeZone.UTC))
        assertTrue(label.contains("۱۴۰۴"))
        assertFalse(label.any { it in '0'..'9' })
    }
}
