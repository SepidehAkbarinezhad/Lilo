package com.sepideh.lilo.core.utils

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getCurrentTime(): Pair<Int, Int> {
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = currentDateTime.hour
    val minute = currentDateTime.minute
    return Pair(hour, minute)
}

@OptIn(ExperimentalTime::class)
fun getCurrentDate(): Long {
    val currentDateTime = Clock.System.now().toEpochMilliseconds()
    return currentDateTime
}

/**
 * Combines [dayMillis] with [hour]/[minute] into a single epoch-millis timestamp
 * usable for scheduling an alarm/notification.
 *
 * Returns null if [dayMillis], [hour], or [minute] is null, or if the resulting
 * timestamp is already in the past (i.e. not safe to schedule).
 */
@OptIn(ExperimentalTime::class)
fun combineDateAndTime(dayMillis: Long?, hour: Int?, minute: Int?): Long? {
    if (dayMillis == null || hour == null || minute == null) return null

    val timeZone = TimeZone.currentSystemDefault()

    val localDateTime = Instant.fromEpochMilliseconds(dayMillis)
        .toLocalDateTime(timeZone)

    val updatedDateTime = LocalDateTime(
        year = localDateTime.year,
        month = localDateTime.month,
        dayOfMonth = localDateTime.dayOfMonth,
        hour = hour,
        minute = minute,
        second = 0,
        nanosecond = 0
    )
    val updatedMillis = updatedDateTime.toInstant(timeZone).toEpochMilliseconds()
    return if (updatedMillis > Clock.System.now().toEpochMilliseconds()) {
        updatedMillis
    } else {
        null
    }
}




