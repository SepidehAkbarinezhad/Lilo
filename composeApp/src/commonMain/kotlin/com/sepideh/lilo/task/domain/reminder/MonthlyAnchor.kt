package com.sepideh.lilo.task.domain.reminder

import kotlinx.datetime.LocalDateTime

enum class ReminderCalendarType { GREGORIAN, JALALI }

/** The selected wall-clock date/time, before any DST adjustment.
 * The date is stored in ISO form; [calendar] determines its recurring month and day.
 * Neither a clamped occurrence nor a language/time-zone change replaces this anchor.
 */
data class MonthlyAnchor(val calendar: ReminderCalendarType, val localDateTime: LocalDateTime)
