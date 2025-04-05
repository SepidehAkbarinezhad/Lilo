package com.sepideh.lilo.task.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID_TAG = "notificationId"
        const val NOTIFICATION_TITLE_TAG = "notificationTitle"
        const val NOTIFICATION_CONTENT_TAG = "notificationContent"
    }

    override fun onReceive(context: Context?, intent: Intent) {
        println("onReceive()")
        showNotification(context, intent)
    }

    private fun showNotification(
        context: Context?,
        intent: Intent,
    ) {
        println("showNotification()")
        val channelId="reminder_channel"
        val title = intent.getStringExtra(NOTIFICATION_TITLE_TAG) ?: ""
        val content = intent.getStringExtra(NOTIFICATION_CONTENT_TAG) ?: ""

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                enableLights(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)

            // Build notification
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("⏰ $content")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .build()

            notificationManager.notify(0, notification)
        }
    }
}