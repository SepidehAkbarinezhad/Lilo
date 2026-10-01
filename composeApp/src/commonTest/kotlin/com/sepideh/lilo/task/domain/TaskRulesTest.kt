package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.domain.model.*
import kotlin.test.*

class TaskRulesTest {
    private val open = Task(id = 1, title = "Practice violin", category = 3, priority = 0)
    private val completed = Task(id = 2, title = "Buy book", category = 4, priority = 2, done = true)

    @Test fun emptyAndBothStatusSelectionsIncludeEveryTask() {
        val tasks = listOf(open, completed)
        assertEquals(2, tasks.matching(TaskQuery()).size)
        assertEquals(2, tasks.matching(TaskQuery(completed = setOf(true, false))).size)
        assertEquals(listOf(completed), tasks.matching(TaskQuery(completed = setOf(true))))
        assertEquals(listOf(open), tasks.matching(TaskQuery(completed = setOf(false))))
    }

    @Test fun searchGroupStatusAndPriorityCompose() {
        val query = TaskQuery(text = " VIOLIN ", groupId = 3, completed = setOf(false), priorities = setOf(TaskPriority.HIGH))
        assertEquals(listOf(open), listOf(open, completed).matching(query))
        assertTrue(listOf(open, completed).matching(query.copy(groupId = 4)).isEmpty())
        assertEquals(2, listOf(open, completed).matching(TaskQuery()).size)
    }

    @Test fun unsupportedPriorityDoesNotCrashOrDependOnListOrder() {
        assertEquals(TaskPriority.MEDIUM, TaskPriority.fromId(999))
        val tasks = listOf(completed, open, Task(id = 3, priority = 999))
        assertEquals(listOf(1L, 3L, 2L), tasks.matching(TaskQuery()).map { it.id })
    }

    @Test fun reminderSortPlacesMissingRemindersLast() {
        val scheduled = open.copy(reminderStartDate = 100, reminderHour = 9, reminderMinute = 0)
        assertEquals(listOf(scheduled, completed), listOf(completed, scheduled).matching(TaskQuery(sort = TaskSort.REMINDER_DATE)))
    }

    @Test fun timestampsSurviveEditingAndTrackReopening() {
        val created = open.withTimestamps(null, 100)
        assertEquals(100L, created.createdAt)
        assertEquals(100L, created.updatedAt)
        val done = created.copy(done = true).withTimestamps(created, 200)
        assertEquals(200L, done.completedAt)
        val edited = done.copy(title = "  Edited  ").withTimestamps(done, 300)
        assertEquals("Edited", edited.title)
        assertEquals(100L, edited.createdAt)
        assertEquals(200L, edited.completedAt)
        assertEquals(300L, edited.updatedAt)
        assertNull(edited.copy(done = false).withTimestamps(edited, 400).completedAt)
    }
}
