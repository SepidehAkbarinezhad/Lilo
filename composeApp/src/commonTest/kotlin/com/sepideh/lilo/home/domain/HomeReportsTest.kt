package com.sepideh.lilo.home.domain

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.note.domain.model.Note
import kotlin.test.*

class HomeReportsTest {
    @Test fun taskPreviewSkipsCompletedActivitiesAndCountsActualRemaining() {
        val report = taskHomeReport(listOf(Task(title = "Old", createdAt = 1),
            Task(title = "Completed", done = true, createdAt = 3), Task(id = 42, title = "Latest", createdAt = 2)))
        assertEquals("Latest", report.nextTaskTitle)
        assertEquals(42L, report.nextTaskId)
        assertEquals(2, report.remainingCount)
        assertEquals(3, report.subTitleReportCount)
    }
    @Test fun completedAndEmptyTaskListsRemainDistinguishable() {
        assertEquals(0, taskHomeReport(emptyList()).subTitleReportCount)
        val completed = taskHomeReport(listOf(Task(done = true)))
        assertEquals(1, completed.subTitleReportCount)
        assertEquals(0, completed.remainingCount)
        assertNull(completed.nextTaskTitle)
        assertNull(completed.nextTaskId)
    }
    @Test fun notePreviewUsesLatestEditRegardlessOfInputOrder() {
        val report = noteHomeReport(listOf(Note(1, "Updated", "Content", createdAt = 1, updatedAt = 3),
            Note(2, "New", "Other", createdAt = 2, updatedAt = 2)))
        assertEquals("Updated", report.latestTitle)
        assertEquals("Content", report.latestSnippet)
        assertEquals(2, report.totalCount)
        assertEquals(0, noteHomeReport(emptyList()).totalCount)
    }

    @Test fun earliestUpcomingTodayWinsOverLatestTaskAndTomorrow() {
        val now = 1_000L
        val report = taskHomeReport(listOf(Task(id = 1, title = "Latest", createdAt = 100),
            Task(id = 2, title = "Later", reminderAt = 20_000, reminderTimeZoneId = "UTC"),
            Task(id = 3, title = "Next", reminderAt = 10_000, reminderTimeZoneId = "UTC"),
            Task(id = 4, title = "Tomorrow", reminderAt = 86_400_000, reminderTimeZoneId = "UTC")),
            now, kotlinx.datetime.TimeZone.UTC)
        assertEquals(3L, report.nextTaskId)
        assertEquals("00:00", report.nextTaskTime)
        assertEquals(4, report.remainingCount)
    }
    @Test fun pastAndCompletedRemindersDoNotDisplaceFallback() {
        val report = taskHomeReport(listOf(Task(id = 1, title = "Latest", createdAt = 100),
            Task(id = 2, title = "Past", reminderAt = 500, reminderTimeZoneId = "UTC"),
            Task(id = 3, title = "Done", done = true, reminderAt = 2000, reminderTimeZoneId = "UTC")),
            1000, kotlinx.datetime.TimeZone.UTC)
        assertEquals(1L, report.nextTaskId)
        assertNull(report.nextTaskTime)
    }
    @Test fun recurringReminderUsesTodaysOccurrence() {
        val task = Task(id = 9, title = "Daily", reminderAt = 10_000, reminderTimeZoneId = "UTC",
            repeatRule = com.sepideh.lilo.task.domain.reminder.RepeatRule.DAILY)
        val report = taskHomeReport(listOf(task), 86_400_000, kotlinx.datetime.TimeZone.UTC)
        assertEquals(9L, report.nextTaskId)
        assertNotNull(report.nextTaskTime)
    }
}
