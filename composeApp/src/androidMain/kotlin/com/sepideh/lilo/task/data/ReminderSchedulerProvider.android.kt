package com.sepideh.lilo.task.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_CONTENT_TAG
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_ID_TAG
import com.sepideh.lilo.task.data.ReminderReceiver.Companion.NOTIFICATION_TITLE_TAG
import com.sepideh.lilo.task.domain.ReminderScheduler

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ReminderManager(private val context: Context) : ReminderScheduler {
    actual override fun scheduleReminder(reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(NOTIFICATION_ID_TAG, reminder.id)
            putExtra(NOTIFICATION_TITLE_TAG, reminder.title)
            putExtra(NOTIFICATION_CONTENT_TAG, reminder.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, reminder.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        /*
        * Starting from Android 12 (API level 31), scheduling exact alarms using methods like
        * setExact() or setExactAndAllowWhileIdle() requires the SCHEDULE_EXACT_ALARM permission.
        * This permission can only be granted manually by the user via the system settings.
        * Therefore, before scheduling an exact alarm, check if your app has permission
        * */
        // TODO: check permision
        try {
            println("ReminderManager try")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+60000, pendingIntent)
        }catch (e:SecurityException){
            // If permission is not granted, direct the user to the settings screen where they can manually get permission
            println("SecurityException : ${e.message}")
        }
    }

    actual override fun cancelReminder(id: String) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}