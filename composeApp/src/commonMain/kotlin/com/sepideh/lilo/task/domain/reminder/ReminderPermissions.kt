package com.sepideh.lilo.task.domain.reminder

interface ReminderPermissions {
    val needsBatteryGuidance: Boolean
    suspend fun hasAccess(): Boolean
    suspend fun requestAccess(firstTime: Boolean)
}
