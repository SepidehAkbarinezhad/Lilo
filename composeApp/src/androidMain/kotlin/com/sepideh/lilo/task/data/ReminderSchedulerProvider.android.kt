package com.sepideh.lilo.task.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.reminder.nextOccurrence
import kotlin.time.Clock

class ReminderSchedulerProvider(private val context: Context) : ReminderScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun scheduleReminder(reminder: Reminder) {
        val next = reminder.nextOccurrence(Clock.System.now().toEpochMilliseconds()) ?: return
        val intent = alarmIntent(reminder.id).putExtra("triggerAt", next)
        val pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // Propagate permission failures to the caller instead of silently losing reminders.
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pending)
    }

    override suspend fun cancelReminder(taskId: Long) {
        PendingIntent.getBroadcast(context, 0, alarmIntent(taskId), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
        NotificationManagerCompat.from(context).cancel("task:$taskId", 0)
    }

    private fun alarmIntent(taskId: Long) = Intent(context, ReminderReceiver::class.java).apply {
        data = Uri.parse("lilo://task-reminder/$taskId")
        putExtra("taskId", taskId)
    }
}
