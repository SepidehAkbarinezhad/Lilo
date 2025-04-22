package com.sepideh.lilo.task.domain.reminder

import java.util.Calendar

actual fun setReminderTime(dayMillis: Long?, hour: Int?, minute: Int?): Long? {
    return dayMillis?.let {
        if (hour != null && minute != null) {
            Calendar.getInstance().apply {
                timeInMillis = it
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

        } else null
    }
}