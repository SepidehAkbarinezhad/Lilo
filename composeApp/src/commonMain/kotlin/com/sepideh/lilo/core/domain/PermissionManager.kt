package com.sepideh.lilo.core.domain

/**
 * A platform-agnostic permission manager to handle runtime permission requests.
 *
 * This expect class abstracts away platform-specific permission logic for features like
 * notifications and exact alarms. On Android, permissions such as POST_NOTIFICATIONS or
 * SCHEDULE_EXACT_ALARM may require checking or guiding the user to settings. On iOS,
 * permissions like notification access must be explicitly requested and are asynchronous.
 *
 * The suspend functions ensure that permission checks or requests can be performed
 * in a coroutine-safe and non-blocking way, regardless of whether the underlying
 * platform APIs are synchronous or asynchronous.
 *
 * Actual implementations should handle platform-specific logic accordingly.
 */
expect class PermissionManager {
    fun isXiaomi(): Boolean
    suspend fun hasAlarmPermission(): Boolean
    suspend fun hasNotificationPermission(): Boolean
    suspend fun requestNeededPermission()
    suspend fun requestDeniedPermission()
}