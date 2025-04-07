package com.sepideh.lilo.task.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_CONTENT_TAG
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_ID_TAG
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_TITLE_TAG
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler

class ReminderManager(private val context: Context) : ReminderScheduler {
    override fun scheduleReminder(reminder: Reminder) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        with(reminder) {
            var triggerTime = startDate
            while (triggerTime <= endDate) {
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra(NOTIFICATION_ID_TAG, reminder.id)
                    putExtra(NOTIFICATION_TITLE_TAG, reminder.title)
                    putExtra(NOTIFICATION_CONTENT_TAG, reminder.content)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, reminder.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                /*
             * Starting from Android 12 (API level 31), scheduling exact alarms using methods like
             * setExact() or setExactAndAllowWhileIdle() requires the SCHEDULE_EXACT_ALARM permission.
             * This permission can only be granted manually by the user via the system settings.
             * Therefore, before scheduling an exact alarm, check if your app has permission
             * */
                // TODO: check permission
                try {
                    println("ReminderManager try")
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                    pendingIntent
                    )
                } catch (e: SecurityException) {
                    // If permission is not granted, direct the user to the settings screen where they can manually get permission
                    println("SecurityException : ${e.message}")
                }
                // Increment by 1 day (in millis)
                triggerTime += 24 * 60 * 60 * 1000L
            }

        }

    }

    override fun cancelReminder(id: String) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}