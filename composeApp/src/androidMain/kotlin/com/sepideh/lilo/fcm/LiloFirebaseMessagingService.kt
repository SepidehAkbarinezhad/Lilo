package com.sepideh.lilo.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class LiloFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "🔄 New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 🔴 CRITICAL - THIS RUNS WHEN APP IS IN FOREGROUND
        Log.d("FCM", "🔥🔥🔥 MESSAGE RECEIVED BY APP! 🔥🔥🔥")

        // Extract notification payload
        val title = remoteMessage.notification?.title ?: "Lilo Update"
        val body = remoteMessage.notification?.body ?: "New content available"

        // 🔴 ACTUALLY SHOW THE NOTIFICATION
        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "lilo_channel"
        val notificationId = System.currentTimeMillis().toInt()

        // Create channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lilo Notifications",
                NotificationManager.IMPORTANCE_HIGH // HIGH = heads-up popup
            ).apply {
                description = "Updates from Lilo app"
                enableLights(true)
                enableVibration(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            Log.d("FCM", "✅ Notification channel created")
        }

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 🔴 REPLACE with your own icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        // SHOW IT!
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)

        Log.e("FCM", "✅ NOTIFICATION DISPLAYED: $title")
    }
}
