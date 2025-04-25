package com.sepideh.lilo.core.domain

actual class PermissionManager {
    actual suspend fun hasAlarmPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun requestAlarmPermission() {
    }

    actual suspend fun hasNotificationPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun requestNotificationPermission() {
    }

}