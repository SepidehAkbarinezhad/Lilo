package com.sepideh.lilo.task.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sepideh.lilo.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID_TAG = "notificationId"
        const val NOTIFICATION_TITLE_TAG = "notificationTitle"
        const val NOTIFICATION_CONTENT_TAG = "notificationContent"
    }

    override fun onReceive(context: Context?, intent: Intent) {
        showNotification(context, intent)
    }

    private fun showNotification(
        context: Context?,
        intent: Intent,
    ) {
        val channelId="reminder_channel"
        val title = intent.getStringExtra(NOTIFICATION_TITLE_TAG) ?: ""
        val content = intent.getStringExtra(NOTIFICATION_CONTENT_TAG) ?: ""
        val id = intent.getIntExtra(NOTIFICATION_ID_TAG,0)

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //to launch the app when user tap on the notification
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .build()

            notificationManager.notify(id, notification)
        }
    }
}