package com.sepideh.lilo.core.service

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri

actual class PermissionManager(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.S)
    actual suspend fun hasAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Below Android 12, no exact alarm permission is needed
            return true
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val canSchedule = alarmManager.canScheduleExactAlarms()
        println("isXiaomi ${isXiaomi()} canSchedule $canSchedule")
        return !isXiaomi() &&  canSchedule
    }

    actual suspend fun hasNotificationPermission(): Boolean {
        // NotificationManagerCompat.areNotificationsEnabled() works correctly across ALL API levels:
        // - API 33+ (Tiramisu): internally checks the POST_NOTIFICATIONS runtime permission grant state
        // - API < 33/Android 13: notifications are granted automatically by the system at install time (no runtime
        //   prompt exists), but the user can still disable them later from the app's Settings screen —
        //   this call correctly detects that manual disable, which checkSelfPermission cannot.
        //
        // This is more reliable than checkSelfPermission(POST_NOTIFICATIONS) alone, since that call
        // is only meaningful on API 33+ and would incorrectly report "granted" on older devices
        // even if the user turned notifications off from Settings.
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }


    actual suspend fun requestNeededPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    actual suspend fun requestDeniedPermission() {
        requestNeededPermission()
    }

    actual fun isXiaomi(): Boolean {
       return  Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)
    }

}