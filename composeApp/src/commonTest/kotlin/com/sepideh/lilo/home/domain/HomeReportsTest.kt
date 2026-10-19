package com.sepideh.lilo.home.domain

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.note.domain.model.Note
import kotlin.test.*

class HomeReportsTest {
    @Test fun taskPreviewShowsLatestAddedEvenWhenCompletedAndCountsActualRemaining() {
        val report = taskHomeReport(listOf(Task(title = "Old", createdAt = 1),
            Task(id = 42, title = "Latest", description = "First line\nSecond line", done = true, createdAt = 3), Task(title = "Earlier unfinished", createdAt = 2)))
        assertEquals("Latest", report.nextTaskTitle)
        assertEquals("First line\nSecond line", report.nextTaskDescription)
        assertTrue(report.nextTaskDone)
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

    @Test fun latestAddedWinsOverUpcomingAndRecurringReminders() {
        val report = taskHomeReport(listOf(
            Task(id = 1, title = "Latest", createdAt = 100),
            Task(id = 2, title = "Soon", createdAt = 30, reminderAt = 10_000, reminderTimeZoneId = "UTC"),
            Task(id = 3, title = "Daily", createdAt = 20, reminderAt = 20_000, reminderTimeZoneId = "UTC",
                repeatRule = com.sepideh.lilo.task.domain.reminder.RepeatRule.DAILY),
        ))
        assertEquals(1L, report.nextTaskId)
        assertNull(report.nextTaskTime)
        assertEquals(3, report.remainingCount)
    }

    @Test fun completionKeepsLatestPreviewAndUpdatesCounts() {
        val latest = Task(id = 2, title = "Latest", createdAt = 20)
        val tasks = listOf(Task(id = 1, title = "Older", createdAt = 10), latest)
        val before = taskHomeReport(tasks)
        val after = taskHomeReport(tasks.map { if (it.id == latest.id) it.copy(done = true, updatedAt = 50) else it })
        assertEquals(before.nextTaskId, after.nextTaskId)
        assertTrue(after.nextTaskDone)
        assertEquals(1, after.remainingCount)
        assertEquals(1, after.completedCount)
    }

    @Test fun creationOrderWinsOverEditsAndUsesIdForTiedTimestamps() {
        val tasks = listOf(Task(id = 1, title = "Old edited", createdAt = 10, updatedAt = 100),
            Task(id = 2, title = "Latest", createdAt = 20), Task(id = 3, title = "Same timestamp", createdAt = 20))
        assertEquals(3L, taskHomeReport(tasks).nextTaskId)
        assertEquals(3L, taskHomeReport(tasks.reversed()).nextTaskId)
    }
}
