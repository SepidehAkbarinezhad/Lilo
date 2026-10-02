package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.reminder.RepeatRule
import com.sepideh.lilo.task.domain.reminder.nextOccurrence
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Clock
import kotlin.time.Instant
import platform.Foundation.*
import platform.UserNotifications.*

class ReminderSchedulerProvider : ReminderScheduler {
    private val center get() = UNUserNotificationCenter.currentNotificationCenter()
    private val prefix = "task-reminder:"

    override suspend fun scheduleReminder(reminder: Reminder) {
        // Normal task mutations call synchronize with the complete list.
        synchronizeRequests(listOf(reminder), "$prefix${reminder.id}:")
    }

    override suspend fun cancelReminder(taskId: Long) {
        val ids = pending().map { it.identifier }.filter { it.startsWith("$prefix$taskId:") }
        center.removePendingNotificationRequestsWithIdentifiers(ids)
        val deliveredIds = delivered().map { it.request.identifier }.filter { it.startsWith("$prefix$taskId:") }
        center.removeDeliveredNotificationsWithIdentifiers(deliveredIds)
    }

    override suspend fun synchronize(reminders: List<Reminder>) = synchronizeRequests(reminders, prefix)

    private suspend fun synchronizeRequests(reminders: List<Reminder>, scope: String) {
        val pending = pending()
        val otherCount = pending.count { !it.identifier.startsWith(scope) }
        val slots = (60 - otherCount).coerceAtLeast(0)
        val now = Clock.System.now().toEpochMilliseconds()
        val candidates = reminders.mapNotNull { r -> r.nextOccurrence(now)?.let { r to it } }.toMutableList()
        require(candidates.size <= slots) { "Too many scheduled reminders for iOS" }
        val firstIds = candidates.map { it.first.id }.toMutableSet()
        val requests = mutableListOf<UNNotificationRequest>()
        while (requests.size < slots && candidates.isNotEmpty()) {
            val candidate = candidates.filter { firstIds.isEmpty() || it.first.id in firstIds }.minBy { it.second }
            firstIds.remove(candidate.first.id)
            candidates.remove(candidate)
            val (reminder, at) = candidate
            val repeatTrigger = if (reminder.repeatRule != RepeatRule.NONE) repeatingTrigger(reminder) else null
            val nativeNext = repeatTrigger?.nextTriggerDate()?.timeIntervalSince1970?.times(1000)?.toLong()
            // A repeating trigger has no start-date bound. Only use it if its NEXT firing
            // matches our first allowed occurrence; otherwise queue bounded dated requests.
            val canRepeat = nativeNext != null && kotlin.math.abs(nativeNext - at) < 1000
            val trigger = if (canRepeat) repeatTrigger!! else oneShotTrigger(at, reminder.timeZoneId)
            val content = UNMutableNotificationContent().apply {
                setTitle(reminder.title); setBody(reminder.content); setSound(UNNotificationSound.defaultSound())
            }
            requests += UNNotificationRequest.requestWithIdentifier("$prefix${reminder.id}:${if (canRepeat) "repeat" else at.toString()}", content, trigger)
            if (!canRepeat) reminder.nextOccurrence(at)?.let { candidates += reminder to it }
        }
        if (reminders.count { it.nextOccurrence(now) != null } > slots) error("Too many scheduled reminders for iOS")
        val desired = requests.map { it.identifier }.toSet()
        center.removePendingNotificationRequestsWithIdentifiers(pending.map { it.identifier }.filter { it.startsWith(scope) && it !in desired })
        requests.forEach { add(it) }
    }

    private fun components(at: Long, zoneId: String): NSDateComponents {
        val local = Instant.fromEpochMilliseconds(at).toLocalDateTime(TimeZone.of(zoneId))
        return NSDateComponents().apply {
            calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
            timeZone = NSTimeZone.timeZoneWithName(zoneId)
            year = local.year.toLong(); month = local.monthNumber.toLong(); day = local.dayOfMonth.toLong()
            hour = local.hour.toLong(); minute = local.minute.toLong(); second = 0
        }
    }

    private fun oneShotTrigger(at: Long, zoneId: String) = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components(at, zoneId), false)

    private fun repeatingTrigger(reminder: Reminder): UNCalendarNotificationTrigger {
        val local = Instant.fromEpochMilliseconds(reminder.reminderAt).toLocalDateTime(TimeZone.of(reminder.timeZoneId))
        val parts = NSDateComponents().apply {
            calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierGregorian)
            timeZone = NSTimeZone.timeZoneWithName(reminder.timeZoneId)
            hour = local.hour.toLong(); minute = local.minute.toLong(); second = 0
            if (reminder.repeatRule == RepeatRule.WEEKLY) weekday = ((local.dayOfWeek.ordinal + 1) % 7 + 1).toLong()
        }
        return UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(parts, true)
    }

    private suspend fun pending(): List<UNNotificationRequest> = suspendCancellableCoroutine { continuation ->
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            if (continuation.isActive) continuation.resume(requests?.filterIsInstance<UNNotificationRequest>() ?: emptyList())
        }
    }

    private suspend fun delivered(): List<UNNotification> = suspendCancellableCoroutine { continuation ->
        center.getDeliveredNotificationsWithCompletionHandler { notifications ->
            if (continuation.isActive) continuation.resume(notifications?.filterIsInstance<UNNotification>() ?: emptyList())
        }
    }

    private suspend fun add(request: UNNotificationRequest): Unit = suspendCancellableCoroutine { continuation ->
        center.addNotificationRequest(request) { error ->
            if (continuation.isActive) {
                if (error == null) continuation.resume(Unit)
                else continuation.resumeWithException(IllegalStateException(error.localizedDescription))
            }
        }
    }
}
