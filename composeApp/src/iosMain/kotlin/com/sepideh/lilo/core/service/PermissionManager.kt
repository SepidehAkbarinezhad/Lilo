package com.sepideh.lilo.core.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

/**
 * iOS implementation of PermissionManager.
 *  Note: iOS handles permissions differently from Android.
 *  - The system permission dialog is only shown **once per permission type**.
 *  - If the user denies the permission the first time, subsequent calls to request it will NOT show the dialog again.
 *  - The user must manually enable the permission from the device settings if they previously denied it.
 */
actual class PermissionManager {

    /**
     * iOS does not have a separate "alarm" permission like Android.
     * This always returns true because alarm functionality is handled through local notifications.
     */
    actual suspend fun hasAlarmPermission(): Boolean {
        return true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun hasNotificationPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter()
                .getNotificationSettingsWithCompletionHandler { settings ->
                    continuation.resume(
                        settings?.authorizationStatus == UNAuthorizationStatusAuthorized,
                        onCancellation = null
                    )
                }
        }
    }

    actual suspend fun requestNeededPermission() {
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or
                        UNAuthorizationOptionSound or
                        UNAuthorizationOptionBadge,
                completionHandler = { granted, error ->
                    println("completionHandler  granted:  $granted  error: $error")
                }
            )
    }


    actual suspend fun requestDeniedPermission() {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(
                url,
                options = emptyMap<Any?, Any>(),
                completionHandler = { success ->
                    println("Opened settings: $success")
                }
            )
        }
    }

    actual fun isXiaomi(): Boolean {
        return false
    }

}