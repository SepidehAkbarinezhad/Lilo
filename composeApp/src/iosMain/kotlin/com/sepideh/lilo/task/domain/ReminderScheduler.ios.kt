package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.data.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class ReminderManager : ReminderScheduler {
    override fun scheduleReminder(reminder: Reminder) {
        with(reminder) {
            if (reminder.endDate != null) {
                // scheduleRangeReminder(reminder)
            } else {
                scheduleSingleReminder(reminder)
            }
        }
    }

    override fun cancelReminder(reminder: Reminder) {

    }

    private fun scheduleSingleReminder(reminder: Reminder) {
        val content = UNMutableNotificationContent()

        content.setTitle(reminder.title ?: "")
        content.setBody(reminder.content ?: "")
        content.setSound(UNNotificationSound.defaultSound())

        reminder.startDate?.let {
            // Convert the startDate from milliseconds to NSDate
            val date = NSDate(it / 1000.0)  // Convert from milliseconds to seconds
            val calendar = NSCalendar.currentCalendar

            // Create a mutable NSDateComponents instance and set the date components
            val components = calendar.components(
                NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                        NSCalendarUnitHour or NSCalendarUnitMinute,
                fromDate = date
            )

            // Create a trigger for the notification based on the NSDateComponents
            val triggerDate = NSDateComponents().apply {
                year = components.year
                month = components.month
                day = components.day
                hour = components.hour
                minute = components.minute
            }

            // Create the notification trigger
            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                triggerDate,
                repeats = false
            )

            // Create the notification request with the identifier and the trigger
            val request = UNNotificationRequest.requestWithIdentifier(
                reminder.id.toString(),
                content,
                trigger
            )

            // Add the notification request to the notification center
            val center = UNUserNotificationCenter.currentNotificationCenter()

            // Correct way to pass a completion handler or null
            center.addNotificationRequest(request) { error ->
                if (error != null) {
                    println("Error adding notification request: ${error.localizedDescription}")
                } else {
                    println("Notification request added successfully!")
                }
            }
        }
    }


}