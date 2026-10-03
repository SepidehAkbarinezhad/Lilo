package com.sepideh.lilo.task.domain.reminder

enum class ReminderPermission { NOTIFICATIONS, EXACT_ALARMS }

interface ReminderPermissions {
    suspend fun missingPermission(): ReminderPermission?
    suspend fun request(permission: ReminderPermission)
    suspend fun hasAccess(): Boolean = missingPermission() == null
}
