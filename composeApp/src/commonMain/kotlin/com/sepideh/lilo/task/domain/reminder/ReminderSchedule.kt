package com.sepideh.lilo.task.domain.reminder

import kotlinx.datetime.*
import kotlin.time.Instant

/** The original instant remains the anchor; recurrence never accumulates DST drift.
 * A nonexistent local time shifts forward through the gap. An overlap fires once,
 * at the earlier offset chosen by kotlinx-datetime.
 */
fun Reminder.nextOccurrence(afterMillis: Long): Long? {
    if (reminderAt > afterMillis) return reminderAt
    if (repeatRule == RepeatRule.NONE) return null
    val zone = TimeZone.of(timeZoneId)
    val anchor = Instant.fromEpochMilliseconds(reminderAt).toLocalDateTime(zone)
    val after = Instant.fromEpochMilliseconds(afterMillis).toLocalDateTime(zone)
    val elapsedDays = anchor.date.daysUntil(after.date).coerceAtLeast(0)
    val step = repeatRule.intervalDays
    var date = anchor.date.plus((elapsedDays / step) * step, DateTimeUnit.DAY)
    while (true) {
        val candidate = LocalDateTime(date, anchor.time).toInstant(zone).toEpochMilliseconds()
        if (candidate > afterMillis && candidate >= reminderAt) return candidate
        date = date.plus(step, DateTimeUnit.DAY)
    }
}
