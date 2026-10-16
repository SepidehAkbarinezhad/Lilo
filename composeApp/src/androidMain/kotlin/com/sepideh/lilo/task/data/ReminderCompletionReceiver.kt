package com.sepideh.lilo.task.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.reminder.COMPLETE_TASK_ACTION
import kotlinx.coroutines.*
import org.koin.mp.KoinPlatform.getKoin

/** Runs without opening an activity, including when the app process was not running. */
class ReminderCompletionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != COMPLETE_TASK_ACTION) return
        val taskId = intent.getLongExtra("taskId", -1)
        if (taskId < 0) return
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                getKoin().get<TaskMutations>().setCompleted(taskId, true)
                // Also dismiss a stale notification whose task has already been deleted.
                NotificationManagerCompat.from(context).cancel("task:$taskId", 0)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { Log.e("LiloReminder", "Could not complete task from notification", failure) }
            finally { pending.finish() }
        }
    }
}
