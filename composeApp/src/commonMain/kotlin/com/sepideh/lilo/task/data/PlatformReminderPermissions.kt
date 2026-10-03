package com.sepideh.lilo.task.data

import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.task.domain.reminder.ReminderPermission
import com.sepideh.lilo.task.domain.reminder.ReminderPermissions

class PlatformReminderPermissions(private val manager: PermissionManager) : ReminderPermissions {
    override suspend fun missingPermission(): ReminderPermission? = when {
        !manager.hasNotificationPermission() -> ReminderPermission.NOTIFICATIONS
        !manager.hasAlarmPermission() -> ReminderPermission.EXACT_ALARMS
        else -> null
    }
    override suspend fun request(permission: ReminderPermission) {
        when (permission) {
            ReminderPermission.NOTIFICATIONS -> manager.requestNotificationAccess()
            ReminderPermission.EXACT_ALARMS -> manager.requestAlarmAccess()
        }
    }
}
