package com.sepideh.lilo.core.domain

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

actual class PermissionManager(private val context: Context) {
    actual suspend fun hasAlarmPermission(): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            false
        }
    }

    actual suspend fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notification permission is automatically granted on lower Android versions
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    actual suspend fun requestNeededPermission() {
        // the flag is required when starting an activity from a non-Activity context (like Application or Service).
        // We're launching the system settings screen from PermissionManager, which uses an application context — so NEW_TASK is necessary.
        // Without it, we will get an IllegalStateException because Android doesn't know how to properly launch the activity in a new task from a non-UI context

        when {
            // If Android version is Android 12 (S), show the Exact Alarm permissions screen
            Build.VERSION.SDK_INT == Build.VERSION_CODES.S -> {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
            // If Android version is Android 13+ (Tiramisu and above), open App Settings for permissions because it needs both alarm and notification setting
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }

            else -> {
                // No need to open anything
            }
        }
    }

}