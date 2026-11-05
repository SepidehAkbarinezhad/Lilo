package com.sepideh.lilo.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sepideh.lilo.R
import com.sepideh.lilo.app.MainActivity
import java.util.UUID

class LiloFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // FCM displays background notification/mixed messages itself. Render only here;
        // never render their data extras again when MainActivity opens.
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) return

        val content = FcmNotificationContent.resolve(
            remoteMessage.data,
            remoteMessage.notification?.title,
            remoteMessage.notification?.body,
            remoteMessage.notification?.imageUrl?.toString(),
            getString(R.string.app_name),
            getString(R.string.fcm_default_body),
        )
        createNotificationChannel(this)
        val image = NotificationImageLoader.load(content.imageUrl)
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(content.title)
            .setContentText(content.body)
            .setSmallIcon(R.drawable.lilo_launcher_monochrome)
            .setContentIntent(openApp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(if (image != null) {
                NotificationCompat.BigPictureStyle().bigPicture(image).setSummaryText(content.body)
            } else {
                NotificationCompat.BigTextStyle().bigText(content.body)
            })
            .build()
        try {
            // Re-delivery of the same message updates its existing notification.
            getSystemService(NotificationManager::class.java).notify(
                remoteMessage.messageId ?: UUID.randomUUID().toString(), 0, notification,
            )
        } catch (_: SecurityException) {
            // Permission may be revoked while the image is loading.
        }
    }

    companion object {
        private const val CHANNEL_ID = "lilo_channel"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, context.getString(R.string.fcm_channel_name),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = context.getString(R.string.fcm_channel_description)
                    enableLights(true)
                    enableVibration(true)
                }
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            }
        }
    }
}
