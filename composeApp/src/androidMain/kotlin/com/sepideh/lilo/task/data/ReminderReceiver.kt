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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sepideh.lilo.app.MainActivity
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.*
import org.koin.mp.KoinPlatform.getKoin

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("taskId", -1)
        val triggerAt = intent.getLongExtra("triggerAt", -1)
        if (taskId < 0 || triggerAt < 0) return
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                getKoin().get<TaskMutations>().deliverReminder(taskId, triggerAt) { showNotification(context, it) }
            } catch (e: Exception) {
                Log.e("LiloReminder", "Reminder delivery failed", e)
            } finally { pending.finish() }
        }
    }

    private fun showNotification(context: Context, task: Task) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            }
            // Existing channel/user sound choices are retained by Android.
            manager.createNotificationChannel(channel)
            manager.getNotificationChannel(channelId)?.let { saved ->
                if (saved.sound == null || saved.importance < NotificationManager.IMPORTANCE_DEFAULT) {
                    Log.w("LiloReminder", "Task reminder channel is silent or low importance; change its sound in notification settings")
                }
            }
        }
        val launch = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle(task.title)
            .setContentText(task.description).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true).setContentIntent(launch).build()
        manager.notify("task:${task.id}", 0, notification)
    }
}

/** Exact alarms are removed at reboot; restore the next occurrence from Room. */
class ReminderRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in setOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_MY_PACKAGE_REPLACED,
                "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED")) return
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try { getKoin().get<TaskMutations>().restoreReminders() }
            catch (e: Exception) { Log.e("LiloReminder", "Could not restore reminders", e) }
            finally { pending.finish() }
        }
    }
}
