package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.domain.reminder.*
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.toLocalDate
import kotlinx.datetime.*
import kotlin.test.*
import kotlin.time.Instant

class MonthlyReminderScheduleTest {
    private fun millis(value: String) = Instant.parse(value).toEpochMilliseconds()
    private fun monthly(local: LocalDateTime, calendar: ReminderCalendarType = ReminderCalendarType.GREGORIAN,
        zone: String = "UTC") = Reminder(1, "Task", "", local.toInstant(TimeZone.of(zone)).toEpochMilliseconds(),
        RepeatRule.MONTHLY, zone, MonthlyAnchor(calendar, local))
    private fun gregorian(value: String, zone: String = "UTC") = monthly(LocalDateTime.parse(value), zone = zone)
    private fun jalali(year: Int, month: Int, day: Int) = monthly(
        LocalDateTime(PersianDateTime(year, month, day).toLocalDate(), LocalTime(9, 15)), ReminderCalendarType.JALALI)
    private fun nextLocal(reminder: Reminder, after: Long = reminder.reminderAt) =
        Instant.fromEpochMilliseconds(requireNotNull(reminder.nextOccurrence(after))).toLocalDateTime(TimeZone.of(reminder.timeZoneId))

    @Test fun january31ClampsToFebruaryThenReturnsTo31() {
        val r = gregorian("2026-01-31T09:15")
        val february = millis("2026-02-28T09:15:00Z")
        assertEquals(february, r.nextOccurrence(r.reminderAt))
        assertEquals(millis("2026-03-31T09:15:00Z"), r.nextOccurrence(february))
        assertEquals(millis("2026-04-30T09:15:00Z"), r.nextOccurrence(millis("2026-03-31T09:15:00Z")))
    }

    @Test fun gregorianLeapYearsIncludeCenturyRule() {
        for ((year, lastDay) in listOf(2024 to 29, 2100 to 28, 2400 to 29)) {
            val r = gregorian("$year-01-31T09:15")
            assertEquals(LocalDate(year, 2, lastDay), nextLocal(r).date)
        }
        val r = gregorian("2024-02-29T09:15")
        assertEquals(LocalDate(2024, 3, 29), nextLocal(r).date)
        val february = requireNotNull(r.nextOccurrence(millis("2025-02-01T00:00:00Z")))
        assertEquals(millis("2025-02-28T09:15:00Z"), february)
        assertEquals(millis("2025-03-29T09:15:00Z"), r.nextOccurrence(february))
    }

    @Test fun yearRolloverAndMissedMonthsKeepOriginalAnchor() {
        val r = gregorian("2020-12-31T09:15")
        assertEquals(LocalDate(2021, 1, 31), nextLocal(r).date)
        assertEquals(millis("2035-03-31T09:15:00Z"), r.nextOccurrence(millis("2035-03-04T10:00:00Z")))
        assertEquals(millis("2035-04-30T09:15:00Z"), r.nextOccurrence(millis("2035-03-31T09:15:00Z")))
    }

    @Test fun futureStartAndExclusiveBoundaryAreRespected() {
        val r = gregorian("2026-01-31T09:15")
        assertEquals(r.reminderAt, r.nextOccurrence(millis("2020-01-01T00:00:00Z")))
        val february = millis("2026-02-28T09:15:00Z")
        assertEquals(february, r.nextOccurrence(february - 1))
        assertEquals(millis("2026-03-31T09:15:00Z"), r.nextOccurrence(february))
    }

    @Test fun jalali31ReturnsAfterTheShorterHalfOfTheYear() {
        val r = jalali(1404, 6, 31)
        assertEquals(LocalDate(2025, 9, 22), r.monthlyAnchor!!.localDateTime.date)
        assertEquals(PersianDateTime(1404, 7, 30).toLocalDate(), nextLocal(r).date)
        val last = requireNotNull(r.nextOccurrence(millis("2026-03-01T00:00:00Z")))
        assertEquals(millis("2026-03-20T09:15:00Z"), last) // 1404/12/29
        assertEquals(PersianDateTime(1405, 1, 31).toLocalDate(), nextLocal(r, last).date)
    }

    @Test fun jalaliLeapEsfandHas30DaysAndNonLeapEsfandHas29() {
        val leap = jalali(1403, 11, 30)
        val leapEnd = millis("2025-03-20T09:15:00Z") // 1403/12/30
        assertEquals(leapEnd, leap.nextOccurrence(leap.reminderAt))
        assertEquals(PersianDateTime(1404, 1, 30).toLocalDate(), nextLocal(leap, leapEnd).date)
        val ordinary = jalali(1404, 11, 30)
        val end = millis("2026-03-20T09:15:00Z") // 1404/12/29
        assertEquals(end, ordinary.nextOccurrence(ordinary.reminderAt))
        assertEquals(PersianDateTime(1405, 1, 30).toLocalDate(), nextLocal(ordinary, end).date)
    }

    @Test fun anchorCreatedOnJalaliLeapDaySurvivesANonLeapYear() {
        val r = jalali(1399, 12, 30)
        assertEquals(LocalDate(2021, 3, 20), r.monthlyAnchor!!.localDateTime.date)
        val nonLeap = millis("2022-03-20T09:15:00Z")
        assertEquals(nonLeap, r.nextOccurrence(millis("2022-03-01T00:00:00Z")))
        assertEquals(PersianDateTime(1401, 1, 30).toLocalDate(), nextLocal(r, nonLeap).date)
    }

    @Test fun calendarsDefineDifferentMonthsFromTheSameCivilStart() {
        val local = LocalDateTime.parse("2026-01-31T09:15")
        val gregorian = monthly(local)
        val jalali = monthly(local, ReminderCalendarType.JALALI)
        assertEquals(millis("2026-02-28T09:15:00Z"), gregorian.nextOccurrence(gregorian.reminderAt))
        assertEquals(millis("2026-03-02T09:15:00Z"), jalali.nextOccurrence(jalali.reminderAt))
    }

    @Test fun springGapShiftsOnlyThatOccurrence() {
        val r = gregorian("2026-02-08T02:30", "America/New_York")
        val gap = millis("2026-03-08T07:30:00Z")
        assertEquals(gap, r.nextOccurrence(r.reminderAt))
        assertEquals(millis("2026-04-08T06:30:00Z"), r.nextOccurrence(gap))
    }

    @Test fun anchorSelectedInsideSpringGapRetainsOriginalWallTime() {
        val r = gregorian("2026-03-08T02:30", "America/New_York")
        assertEquals(millis("2026-03-08T07:30:00Z"), r.reminderAt)
        assertEquals(millis("2026-04-08T06:30:00Z"), r.nextOccurrence(r.reminderAt))
        assertEquals(2, r.monthlyAnchor!!.localDateTime.hour)
    }

    @Test fun autumnOverlapFiresOnceAtEarlierOffset() {
        val r = gregorian("2026-10-01T01:30", "America/New_York")
        val first = millis("2026-11-01T05:30:00Z")
        assertEquals(first, r.nextOccurrence(r.reminderAt))
        val december = millis("2026-12-01T06:30:00Z")
        assertEquals(december, r.nextOccurrence(first))
        assertEquals(december, r.nextOccurrence(millis("2026-11-01T06:00:00Z")))
    }

    @Test fun jalaliRecurrenceAlsoUsesTheSavedZoneAcrossDst() {
        val local = LocalDateTime(PersianDateTime(1404, 11, 17).toLocalDate(), LocalTime(9, 15))
        val r = monthly(local, ReminderCalendarType.JALALI, "America/New_York")
        val after = requireNotNull(r.nextOccurrence(r.reminderAt))
        assertEquals(LocalTime(9, 15), nextLocal(r).time)
        assertEquals(PersianDateTime(1404, 12, 17).toLocalDate(), nextLocal(r).date)
        assertEquals(LocalTime(9, 15), nextLocal(r, after).time)
        assertEquals(PersianDateTime(1405, 1, 17).toLocalDate(), nextLocal(r, after).date)
    }

    @Test fun monthlyRequiresCompleteConsistentMetadata() {
        val r = gregorian("2026-01-31T09:15")
        assertFailsWith<IllegalArgumentException> { r.copy(monthlyAnchor = null).nextOccurrence(0) }
        assertFailsWith<IllegalArgumentException> { r.copy(reminderAt = r.reminderAt + 1).nextOccurrence(0) }
    }
}
