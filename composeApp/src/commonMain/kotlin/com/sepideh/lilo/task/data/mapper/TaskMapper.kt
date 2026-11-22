package com.sepideh.lilo.task.data.mapper

import com.sepideh.lilo.task.data.local.room.TaskEntity
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.MonthlyAnchor
import com.sepideh.lilo.task.domain.reminder.ReminderCalendarType
import kotlinx.datetime.LocalDateTime
import com.sepideh.lilo.task.domain.reminder.RepeatRule
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    done = done,
    groupId = groupId ?: 0,
    priority = priority.toTaskPriority().toStorageCode(),
    reminderAt = reminderAt,
    repeatRule = RepeatRule.fromCode(repeatRule),
    reminderTimeZoneId = reminderTimeZoneId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
    imageNames = Json.decodeFromString<List<String>>(imageNames),
    monthlyAnchor = if (repeatRule == RepeatRule.MONTHLY.name) MonthlyAnchor(
        ReminderCalendarType.valueOf(requireNotNull(reminderCalendar)),
        LocalDateTime.parse(requireNotNull(reminderLocalDateTime)),
    ) else null,
)

fun List<TaskEntity>.toTaskList() = this.map { it.toTask() }

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id ?: 0,
    title = title,
    description = description,
    done = done,
    groupId = groupId.takeUnless { it == 0L },
    priority = priority.toTaskPriority().toStorageCode(),
    reminderAt = reminderAt,
    repeatRule = repeatRule.name,
    reminderTimeZoneId = reminderTimeZoneId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
    imageNames = Json.encodeToString(imageNames),
    reminderCalendar = monthlyAnchor?.calendar?.name,
    reminderLocalDateTime = monthlyAnchor?.localDateTime?.toString(),
)
