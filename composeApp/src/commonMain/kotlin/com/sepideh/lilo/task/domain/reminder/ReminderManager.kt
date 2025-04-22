package com.sepideh.lilo.task.domain.reminder

import com.sepideh.lilo.task.presentation.task_detail.ReminderModel

expect fun setReminderTime(dayMillis: Long?, hour: Int?, minute: Int?) : Long?