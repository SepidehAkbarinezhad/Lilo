package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.data.mapper.toEntity
import com.sepideh.lilo.task.data.mapper.toTask
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import kotlin.test.*
import kotlin.time.Instant

class MonthlyReminderMutationTest {
    private fun millis(value: String) = Instant.parse(value).toEpochMilliseconds()
    private fun task(calendar: ReminderCalendarType = ReminderCalendarType.GREGORIAN): Task {
        val local = LocalDateTime.parse("2026-01-31T09:15")
        return Task(id = 1, title = "Original", reminderAt = local.toInstant(TimeZone.of("Asia/Tehran")).toEpochMilliseconds(),
            reminderTimeZoneId = "Asia/Tehran", repeatRule = RepeatRule.MONTHLY,
            monthlyAnchor = MonthlyAnchor(calendar, local), imageNames = listOf("kept.jpg"))
    }

    @Test fun persistenceAndContentEditingKeepTheCompleteAnchorForBothCalendars() = runTest {
        for (calendar in ReminderCalendarType.entries) {
            val original = task(calendar)
            assertEquals(original, original.toEntity().toTask())
            val repo = Repo(original)
            val scheduler = Scheduler()
            val now = millis("2026-03-01T00:00:00Z")
            val mutations = TaskMutations(repo, scheduler, now = { now })
            mutations.save(original.copy(title = "Edited"))
            assertEquals(original.monthlyAnchor, repo.row?.monthlyAnchor)
            assertEquals(original.reminderTimeZoneId, repo.row?.reminderTimeZoneId)
            assertEquals(original.asReminder()!!.nextOccurrence(now), scheduler.scheduled.single().nextOccurrence(now))
        }
    }

    @Test fun dstGapAnchorSurvivesPersistenceWithoutBecomingTheShiftedTime() {
        val local = LocalDateTime.parse("2026-03-08T02:30")
        val original = task().copy(reminderAt = local.toInstant(TimeZone.of("America/New_York")).toEpochMilliseconds(),
            reminderTimeZoneId = "America/New_York", monthlyAnchor = MonthlyAnchor(ReminderCalendarType.GREGORIAN, local))
        val restored = original.toEntity().toTask()
        assertEquals(local, restored.monthlyAnchor!!.localDateTime)
        assertEquals(millis("2026-04-08T06:30:00Z"), restored.asReminder()!!.nextOccurrence(restored.reminderAt!!))
    }

    @Test fun editingMonthlyDateReplacesAnchorAndRejectsAnOldPendingAlarm() = runTest {
        val original = task()
        val repo = Repo(original)
        val scheduler = Scheduler()
        val mutations = TaskMutations(repo, scheduler, now = { millis("2026-02-01T00:00:00Z") })
        val local = LocalDateTime.parse("2026-02-15T10:45")
        val edited = original.copy(reminderAt = local.toInstant(TimeZone.of("Asia/Tehran")).toEpochMilliseconds(),
            monthlyAnchor = MonthlyAnchor(ReminderCalendarType.GREGORIAN, local))
        mutations.save(edited)
        val oldAt = requireNotNull(original.asReminder()!!.nextOccurrence(original.reminderAt!!))
        var delivered = false
        mutations.deliverReminder(1, oldAt) { delivered = true }
        assertFalse(delivered)
        assertEquals(edited.monthlyAnchor, repo.row?.monthlyAnchor)
        assertEquals(millis("2026-03-15T07:15:00Z"), repo.row!!.asReminder()!!.nextOccurrence(edited.reminderAt!!))
        mutations.deliverReminder(1, edited.reminderAt) { delivered = true }
        assertTrue(delivered)
        assertEquals(edited.monthlyAnchor, scheduler.scheduled.last().monthlyAnchor)
    }

    @Test fun clearingReminderAndCompletionPreventDeliveryAndRestoration() = runTest {
        val original = task()
        val repo = Repo(original)
        val scheduler = Scheduler()
        val mutations = TaskMutations(repo, scheduler)
        mutations.setCompleted(1, true)
        assertTrue(scheduler.scheduled.isEmpty())
        mutations.deliverReminder(1, original.reminderAt!!) { fail("Completed task delivered") }
        mutations.setCompleted(1, false)
        assertEquals(original.monthlyAnchor, scheduler.scheduled.single().monthlyAnchor)
        mutations.save(repo.row!!.copy(reminderAt = null, repeatRule = RepeatRule.NONE, reminderTimeZoneId = null, monthlyAnchor = null))
        assertTrue(scheduler.scheduled.isEmpty())
        mutations.restoreReminders()
        assertTrue(scheduler.scheduled.isEmpty())
        assertNull(repo.row!!.toEntity().reminderCalendar)
        assertNull(repo.row!!.toEntity().reminderLocalDateTime)
    }

    @Test fun unsupportedSchedulerNeverReceivesMonthlyButCanRetainAnExistingRule() = runTest {
        val original = task()
        val repo = Repo(original)
        val scheduler = Scheduler(supportsMonthlyReminders = false)
        val mutations = TaskMutations(repo, scheduler)
        assertFalse(mutations.supportsMonthlyReminders)
        mutations.restoreReminders()
        mutations.updateNotificationPreference { }
        mutations.deliverReminder(1, original.reminderAt!!) { fail("Unsupported reminder delivered") }
        mutations.save(original.copy(title = "Content edit"))
        assertTrue(scheduler.scheduled.isEmpty())
        assertEquals(original.monthlyAnchor, repo.row?.monthlyAnchor)
        assertFailsWith<IllegalArgumentException> { mutations.save(original.copy(id = null)) }
        assertFailsWith<IllegalArgumentException> { mutations.save(original.copy(monthlyAnchor =
            original.monthlyAnchor!!.copy(calendar = ReminderCalendarType.JALALI))) }
        assertEquals("Content edit", repo.row?.title)
        val daily = original.copy(repeatRule = RepeatRule.DAILY, monthlyAnchor = null)
        mutations.save(daily)
        assertEquals(RepeatRule.DAILY, scheduler.scheduled.single().repeatRule)
    }

    private class Scheduler(override val supportsMonthlyReminders: Boolean = true) : ReminderScheduler {
        val scheduled = mutableListOf<Reminder>()
        override suspend fun scheduleReminder(reminder: Reminder) { scheduled += reminder }
        override suspend fun cancelReminder(taskId: Long) { scheduled.removeAll { it.id == taskId } }
        override suspend fun synchronize(reminders: List<Reminder>) { scheduled.clear(); scheduled += reminders }
        override suspend fun refreshNotificationPreference(reminders: suspend () -> List<Reminder>) { synchronize(reminders()) }
    }

    private class Repo(var row: Task?) : TaskRepository {
        override fun getAllTasks() = flowOf(listOfNotNull(row))
        override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
        override suspend fun getTaskById(id: Long) = row?.takeIf { it.id == id }
        override suspend fun clearGroup(id: Long) = Unit
        override suspend fun deleteTask(id: Long) { row = null }
        override suspend fun upsertTask(task: Task): Long { row = task.copy(id = task.id ?: 1); return row!!.id!! }
    }
}
