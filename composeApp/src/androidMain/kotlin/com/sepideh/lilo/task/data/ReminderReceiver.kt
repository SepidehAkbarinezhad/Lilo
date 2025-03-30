package com.sepideh.lilo.task.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val REMINDER_ID_TAG = "reminderId"
        const val REMINDER_TITLE_TAG = "reminderTitle"
    }

    override fun onReceive(context: Context?, intent: Intent) {

        println("onReceive()")
        val reminderId = intent.getStringExtra(REMINDER_ID_TAG) ?: return
        val title = intent.getStringExtra(REMINDER_TITLE_TAG) ?: "Reminder"
        showNotification(context, reminderId, title)
    }

    private fun showNotification(
        context: Context?,
        reminderId: String,
        title: String,
    ) {
        println("showNotification()")
    }
}