package com.sepideh.lilo.core.notifications

import com.sepideh.lilo.task.data.NotificationDelegate
import platform.UserNotifications.UNUserNotificationCenter

/**
 * Initializes the notification delegate for iOS.
 *
 * By default, iOS does not display notifications when the app is in the foreground.
 * Setting a UNUserNotificationCenter delegate allows us to decide how to present
 * those notifications (e.g., showing banners, sounds, etc.).
 *
 * ⚠️ This should be called once at app startup (e.g., in AppDelegate or MainViewController),
 *  otherwise, foreground notifications may be lost.
 */

fun initNotificationDelegate() {
    UNUserNotificationCenter.currentNotificationCenter().delegate = NotificationDelegate()
}
