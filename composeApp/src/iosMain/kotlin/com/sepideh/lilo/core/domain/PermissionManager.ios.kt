package com.sepideh.lilo.core.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.*


actual class PermissionManager {
    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun hasNotificationPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter()
                .getNotificationSettingsWithCompletionHandler { settings ->
                    continuation.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized, onCancellation = null)
                }
        }
    }

    actual suspend fun requestNeededPermission() {
        println("requestNeededPermission()")

        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or
                        UNAuthorizationOptionSound or
                        UNAuthorizationOptionBadge,
                completionHandler = { granted, error ->
                    println("completionHandler  granted:  $granted  error: $error")
                    // Optionally handle this result
                }
            )
    }
    actual suspend fun hasAlarmPermission(): Boolean {
        return true
    }

}