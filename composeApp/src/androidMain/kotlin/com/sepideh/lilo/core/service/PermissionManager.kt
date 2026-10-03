package com.sepideh.lilo.core.service

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

actual class PermissionManager(private val context: Context) {

    actual suspend fun hasAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Below Android 12, no exact alarm permission is needed
            return true
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    actual suspend fun hasNotificationPermission(): Boolean {
        return androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled() && if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API 33+), notification is a runtime permission
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true  // For Android 12 and lower, permission is always granted automatically
        }
    }

    actual suspend fun requestNeededPermission() {
        // the flag is required when starting an activity from a non-Activity context (like Application or Service).
        // We're launching the system settings screen from PermissionManager, which uses an application context — so NEW_TASK is necessary.
        // Without it, we will get an IllegalStateException because Android doesn't know how to properly launch the activity in a new task from a non-UI context

        when {
            // Android 12 (S), show the Exact Alarm permissions screen
            // Notification permission is not needed on this version
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasAlarmPermission() -> {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
            // Android 13+ (Tiramisu): open App Settings because both alarm & notification may need user action
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }

            else -> {
                // Android <12: no explicit alarm or notification permissions are needed
            }
        }
    }

    actual suspend fun requestDeniedPermission() {
        requestNeededPermission()
    }

    actual suspend fun requestNotificationAccess() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    actual suspend fun requestAlarmAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    actual fun isXiaomi(): Boolean {
       return  Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)
    }

}