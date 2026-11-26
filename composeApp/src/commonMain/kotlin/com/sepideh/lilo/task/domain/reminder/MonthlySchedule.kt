package com.sepideh.lilo.task.domain.reminder

import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.monthLength
import io.github.faridsolgi.persiandatetime.extensions.toLocalDate
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import kotlinx.datetime.*
import kotlin.time.Instant

internal fun MonthlyAnchor.nextOccurrence(reminderAt: Long, zone: TimeZone, afterMillis: Long): Long {
    require(localDateTime.toInstant(zone).toEpochMilliseconds() == reminderAt) {
        "Monthly reminder instant must match its original local date/time and zone"
    }
    if (reminderAt > afterMillis) return reminderAt
    val after = Instant.fromEpochMilliseconds(afterMillis).toLocalDateTime(zone).date
    val anchorDate = localDateTime.date
    val anchorMonth = monthIndex(anchorDate)
    var month = maxOf(anchorMonth, monthIndex(after))
    while (true) {
        val year = month.floorDiv(12)
        val number = month.mod(12) + 1
        val date = when (calendar) {
            ReminderCalendarType.GREGORIAN -> {
                val first = LocalDate(year, number, 1)
                val length = first.daysUntil(first.plus(1, DateTimeUnit.MONTH))
                LocalDate(year, number, minOf(anchorDate.day, length))
            }
            ReminderCalendarType.JALALI -> {
                val first = PersianDateTime(year, number, 1)
                val day = minOf(anchorDate.toPersianDateTime().day, first.monthLength())
                // Compute the target Jalali year/month directly; the library's plus(MONTH)
                // uses Gregorian month arithmetic and would change the recurrence rule.
                PersianDateTime(year, number, day).toLocalDate()
            }
        }
        val candidate = LocalDateTime(date, localDateTime.time).toInstant(zone).toEpochMilliseconds()
        if (candidate > afterMillis && candidate >= reminderAt) return candidate
        month++
    }
}

private fun MonthlyAnchor.monthIndex(date: LocalDate): Int = when (calendar) {
    ReminderCalendarType.GREGORIAN -> date.year * 12 + date.month.number - 1
    ReminderCalendarType.JALALI -> date.toPersianDateTime().let { it.year * 12 + it.month - 1 }
}
