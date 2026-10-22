package com.sepideh.lilo.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

class LiloFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Messaging token refreshed")
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
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
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
        try {
            manager.notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Notification permission may be revoked after the check above.
            return
        }

        Log.d("FCM", "Notification displayed")
    }
}
