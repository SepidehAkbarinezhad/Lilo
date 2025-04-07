package com.sepideh.lilo.task.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_CONTENT_TAG
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_ID_TAG
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_TITLE_TAG
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler

class ReminderManager(private val context: Context) : ReminderScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleReminder(reminder: Reminder) {
        /*
        * - Starting from Android 12 (API level 31), scheduling exact alarms using methods like
        * setExact() or setExactAndAllowWhileIdle() requires the SCHEDULE_EXACT_ALARM permission.
        * This permission can only be granted manually by the user via the system settings.
        * Therefore, before scheduling an exact alarm, check if your app has permission
        * - use setExactAndAllowWhileIdle to deliver this alarm exactly at this time, even if devise in Doze
        * */
        with(reminder) {
            if (endDate != null) {
                setAlarmForRangeDay(reminder)
            } else {
                setAlarmForSingleDay(reminder)
            }
        }

    }

    private fun setAlarmForRangeDay(reminder: Reminder) {
        with(reminder) {
            var triggerTime = startDate
            while (triggerTime <= endDate!!) {
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra(NOTIFICATION_ID_TAG, reminder.id)
                    putExtra(NOTIFICATION_TITLE_TAG, reminder.title)
                    putExtra(NOTIFICATION_CONTENT_TAG, reminder.content)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, reminder.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
                )

                // TODO: check permission
                try {
                    println("ReminderManager try")
                    //
                    alarmManager.setExactAndAllowWhileIdle(
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

    private fun setAlarmForSingleDay(reminder: Reminder) {
        /*
        * For repeating alarms, provide a cancel button to allow the user to stop the repetition.
        * For one-time trigger alarms, no need to cancel unless required after it triggers.
        */
        with(reminder) {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(NOTIFICATION_ID_TAG, id)
                putExtra(NOTIFICATION_TITLE_TAG, title)
                putExtra(NOTIFICATION_CONTENT_TAG, content)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                println("ReminderManager try")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    startDate,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // If permission is not granted, direct the user to the settings screen where they can manually get permission
                println("SecurityException : ${e.message}")
            }
        }

    }

    override fun cancelReminder(reminder: Reminder) {
        // Create a new PendingIntent with the same requestCode (id) to cancel the alarm.

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(NOTIFICATION_ID_TAG, reminder.id)
            putExtra(NOTIFICATION_TITLE_TAG, reminder.title)
            putExtra(NOTIFICATION_CONTENT_TAG, reminder.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id, // Unique requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        NotificationManagerCompat.from(context).cancel(reminder.id)
    }
}