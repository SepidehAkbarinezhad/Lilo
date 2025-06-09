package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter


class ReminderSchedulerProvider : ReminderScheduler {
    private val notificationDelegate = NotificationDelegate()
    init {
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound
            ) { granted, error ->
                println("Notification permission granted: $granted, error: $error")
            }

        // Set delegate to show notifications in foreground
        UNUserNotificationCenter.currentNotificationCenter().delegate = notificationDelegate
    }

    override fun scheduleReminder(reminder: Reminder) {
        reminder.startDate?.let { start ->
            if (reminder.endDate != null) {
                scheduleRangeReminders(reminder)
            } else {
                scheduleSingleReminder(reminder, start)
            }
        }
    }

    private fun scheduleSingleReminder(reminder: Reminder, timestampMillis: Long) {
        val dateComponents = timestampMillis.toNSDateComponents()
        val content = createNotificationContent(reminder)

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = reminder.id.toString(),
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                error?.let { println("iOS: Failed to schedule single reminder: $it") }
            }
    }

    private fun scheduleRangeReminders(reminder: Reminder) {
        var current = reminder.startDate!!
        val end = reminder.endDate!!

        while (current <= end) {
            val dateComponents = current.toNSDateComponents()
            val content = createNotificationContent(reminder)

            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = dateComponents,
                repeats = false
            )

            val identifier = "${reminder.id}_$current"
            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = identifier,
                content = content,
                trigger = trigger
            )

            UNUserNotificationCenter.currentNotificationCenter()
                .addNotificationRequest(request) { error ->
                    error?.let { println("iOS: Failed to schedule range reminder: $it") }
                }

            // Increment by 1 day
            current += 24 * 60 * 60 * 1000
        }
    }

    override fun cancelReminder(reminder: Reminder) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        if (reminder.endDate != null) {
            val identifiers = mutableListOf<String>()
            var current = reminder.startDate!!
            val end = reminder.endDate!!

            while (current <= end) {
                identifiers += "${reminder.id}_$current"
                current += 24 * 60 * 60 * 1000
            }

            center.removePendingNotificationRequestsWithIdentifiers(identifiers)
        } else {
            center.removePendingNotificationRequestsWithIdentifiers(listOf(reminder.id.toString()))
        }
    }

    private fun createNotificationContent(reminder: Reminder): UNMutableNotificationContent {
        val content = UNMutableNotificationContent()
        content.setTitle(reminder.title)
        content.setBody(reminder.content ?: "")
        content.setSound(UNNotificationSound.defaultSound())
        return content
    }
}

fun Long.toNSDateComponents(): NSDateComponents {
    val calendar = NSCalendar.currentCalendar
    calendar.timeZone = NSTimeZone.localTimeZone // 👈 Use local time zone

    val date = NSDate.dateWithTimeIntervalSince1970(this / 1000.0)
    val components = calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond,
        fromDate = date
    )!!
    components.setTimeZone(NSTimeZone.localTimeZone)
    return components
}