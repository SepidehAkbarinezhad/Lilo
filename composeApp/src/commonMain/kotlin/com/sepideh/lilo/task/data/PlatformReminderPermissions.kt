package com.sepideh.lilo.task.data

import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.task.domain.reminder.ReminderPermissions

class PlatformReminderPermissions(private val manager: PermissionManager) : ReminderPermissions {
    override val needsBatteryGuidance: Boolean get() = manager.isXiaomi()
    override suspend fun hasAccess(): Boolean = manager.hasNotificationPermission() &&
        manager.hasAlarmPermission()
    override suspend fun requestAccess(firstTime: Boolean) {
        if (firstTime) manager.requestNeededPermission() else manager.requestDeniedPermission()
    }
}
