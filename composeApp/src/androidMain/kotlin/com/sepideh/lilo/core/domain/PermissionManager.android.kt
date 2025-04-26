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

    @RequiresApi(Build.VERSION_CODES.S)
    actual suspend fun requestAlarmPermission() {
        // the flag is required when starting an activity from a non-Activity context (like Application or Service).
        // You're launching the system settings screen for exact alarm permissions (Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        // from your PermissionManager, which likely uses an application context — so NEW_TASK is necessary.
        // This flag ensures the activity is launched in a new task stack and prevents exceptions when using startActivity().
        // Without it, the system will throw an exception if you try to start the activity from a non-Activity context.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    actual suspend fun hasNotificationPermission(): Boolean {
        return true
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        } else {
//            true // Notification permission is automatically granted on lower Android versions
//        }

    }

    actual suspend fun requestNotificationPermission() {
    }
}