package com.sepideh.lilo.task.domain.reminder

import com.sepideh.lilo.task.domain.model.Task

data class Reminder(
    val id: Long,
    val title: String,
    val content: String,
    val reminderAt: Long,
    val repeatRule: RepeatRule,
    val timeZoneId: String,
)

fun Task.asReminder(): Reminder? {
    val at = reminderAt ?: return null
    return Reminder(requireNotNull(id), title, description, at, repeatRule, requireNotNull(reminderTimeZoneId))
}
