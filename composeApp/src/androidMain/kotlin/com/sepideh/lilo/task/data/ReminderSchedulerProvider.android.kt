package com.sepideh.lilo.task.data

import android.content.Context
import com.sepideh.lilo.task.domain.ReminderScheduler

actual fun getReminderScheduler(context: Any): ReminderScheduler {
    require(context is Context) { "Expected Android Context" }
    return AndroidReminderScheduler(context)
}