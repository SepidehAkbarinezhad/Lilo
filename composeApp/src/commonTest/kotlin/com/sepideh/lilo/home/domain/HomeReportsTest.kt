package com.sepideh.lilo.home.domain

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.note.domain.model.Note
import kotlin.test.*

class HomeReportsTest {
    @Test fun taskPreviewSkipsCompletedActivitiesAndCountsActualRemaining() {
        val report = taskHomeReport(listOf(Task(title = "Old", createdAt = 1),
            Task(title = "Completed", done = true, createdAt = 3), Task(id = 42, title = "Latest", description = "First line\nSecond line", createdAt = 2)))
        assertEquals("Latest", report.nextTaskTitle)
        assertEquals("First line\nSecond line", report.nextTaskDescription)
        assertFalse(report.nextTaskDone)
        assertEquals(42L, report.nextTaskId)
        assertEquals(2, report.remainingCount)
        assertEquals(1, report.completedCount)
        assertEquals(0, report.nextTaskPriority)
        assertEquals(3, report.subTitleReportCount)
    }
    @Test fun emptyTaskListHasNoPreview() {
        val report = taskHomeReport(emptyList())
        assertEquals(0, report.subTitleReportCount)
        assertNull(report.nextTaskTitle)
        assertNull(report.nextTaskId)
        assertFalse(report.nextTaskDone)
        assertEquals("", report.nextTaskDescription)
    }
    @Test fun allCompletedShowsNewestCreatedTaskEvenIfOlderTaskWasFinishedLater() {
        val report = taskHomeReport(listOf(
            Task(id = 1, title = "Old", done = true, createdAt = 1, updatedAt = 100, completedAt = 100),
            Task(id = 2, title = "Newest", description = "Completed description", done = true, createdAt = 3, completedAt = 4, priority = 2),
            Task(id = 3, title = "Middle", done = true, createdAt = 2, completedAt = 5),
        ))
        assertEquals("Newest", report.nextTaskTitle)
        assertEquals(2L, report.nextTaskId)
        assertTrue(report.nextTaskDone)
        assertEquals("Completed description", report.nextTaskDescription)
        assertEquals(2, report.nextTaskPriority)
        assertNull(report.nextTaskTime)
        assertEquals(0, report.remainingCount)
        assertEquals(3, report.completedCount)
    }
    @Test fun notePreviewUsesLatestEditRegardlessOfInputOrder() {
        val report = noteHomeReport(listOf(Note(1, "Updated", "Content", createdAt = 1, updatedAt = 3),
            Note(2, "New", "Other", createdAt = 2, updatedAt = 2)))
        assertEquals("Updated", report.latestTitle)
        assertEquals(1L, report.latestNoteId)
        assertNull(noteHomeReport(emptyList()).latestNoteId)
        assertEquals("Content", report.latestSnippet)
        assertEquals(2, report.totalCount)
        assertEquals(3L, report.latestUpdatedAt)
        assertNull(noteHomeReport(emptyList()).latestUpdatedAt)
        assertEquals(0, noteHomeReport(emptyList()).totalCount)
    }

    @Test fun notePreviewFallsBackToCreationWhenEditDateIsMissing() {
        val report = noteHomeReport(listOf(Note(id = 1, createdAt = 20), Note(id = 2, createdAt = 1, updatedAt = 10)))
        assertEquals(1L, report.latestNoteId)
        assertEquals(20L, report.latestUpdatedAt)
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
