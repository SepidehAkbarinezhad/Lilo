package com.sepideh.lilo.task.data


import platform.UserNotifications.*
import platform.darwin.NSObject

class NotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        // Show notification as alert and play sound even if app is foreground
        withCompletionHandler(UNNotificationPresentationOptionAlert or UNNotificationPresentationOptionSound)
    }
}