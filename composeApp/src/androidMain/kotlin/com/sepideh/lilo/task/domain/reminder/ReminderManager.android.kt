package com.sepideh.lilo.task.domain.reminder

import com.sepideh.lilo.task.presentation.task_detail.ReminderModel
import java.util.Calendar

actual fun setReminderTime(timeModel: ReminderModel?): Long? {
    return timeModel?.let {
        if (it.startDay != null && it.hour != null && it.minute != null) {
            Calendar.getInstance().apply {
                timeInMillis = it.startDay
                set(Calendar.HOUR_OF_DAY, it.hour)
                set(Calendar.MINUTE, it.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

        } else null
    }
}