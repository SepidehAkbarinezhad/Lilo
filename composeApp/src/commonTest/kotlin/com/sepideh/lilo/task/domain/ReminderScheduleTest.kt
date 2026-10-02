package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.domain.reminder.*
import kotlin.test.*
import kotlin.time.Instant

class ReminderScheduleTest {
    private fun millis(value: String) = Instant.parse(value).toEpochMilliseconds()
    private fun reminder(at: String, rule: RepeatRule = RepeatRule.DAILY, zone: String = "UTC") =
        Reminder(1, "Task", "", millis(at), rule, zone)

    @Test fun oneTimeReminderIsNotRepeated() {
        val r = reminder("2026-10-02T13:30:00Z", RepeatRule.NONE)
        assertEquals(r.reminderAt, r.nextOccurrence(r.reminderAt - 1))
        assertNull(r.nextOccurrence(r.reminderAt))
    }

    @Test fun futureStartIsNeverScheduledEarly() {
        val r = reminder("2026-10-10T13:30:00Z")
        assertEquals(r.reminderAt, r.nextOccurrence(millis("2026-10-02T10:00:00Z")))
    }

    @Test fun missedDaysAreSkippedWithoutChangingTime() {
        val r = reminder("2026-10-02T13:30:00Z")
        assertEquals(millis("2026-10-09T13:30:00Z"), r.nextOccurrence(millis("2026-10-08T15:00:00Z")))
    }

    @Test fun weeklyKeepsTheAnchorWeekday() {
        val r = reminder("2026-10-02T13:30:00Z", RepeatRule.WEEKLY)
        assertEquals(millis("2026-10-16T13:30:00Z"), r.nextOccurrence(millis("2026-10-10T10:00:00Z")))
    }

    @Test fun dailyPreservesWallClockAcrossSpringDst() {
        val r = reminder("2026-03-07T18:30:00Z", zone = "America/New_York")
        assertEquals(millis("2026-03-08T17:30:00Z"), r.nextOccurrence(r.reminderAt))
    }

    @Test fun nonexistentTimeShiftsForwardThenReturnsToAnchorTime() {
        val r = reminder("2026-03-07T07:30:00Z", zone = "America/New_York")
        val shifted = millis("2026-03-08T07:30:00Z")
        assertEquals(shifted, r.nextOccurrence(r.reminderAt))
        assertEquals(millis("2026-03-09T06:30:00Z"), r.nextOccurrence(shifted))
    }

    @Test fun repeatedAutumnHourFiresOnlyOnce() {
        val r = reminder("2026-10-31T05:30:00Z", zone = "America/New_York")
        val first = millis("2026-11-01T05:30:00Z")
        assertEquals(first, r.nextOccurrence(r.reminderAt))
        assertEquals(millis("2026-11-02T06:30:00Z"), r.nextOccurrence(first))
    }
}
