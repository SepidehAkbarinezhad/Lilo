package com.sepideh.lilo.task.domain

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.reminder.RepeatRule
import com.sepideh.lilo.task.domain.reminder.nextOccurrence
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.usecase.ExpiredReminderException
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TaskMutationConcurrencyTest {
    private val at = 2_000_000_000_000L
    private fun task(id: Long = 1) = Task(id = id, title = "Original", reminderAt = at,
        reminderTimeZoneId = "UTC", repeatRule = RepeatRule.DAILY, imageNames = listOf("old.jpg"))

    @Test fun modifiedOneTimeReminderExpiringDuringImportCannotReplaceStoredSchedule() = runTest {
        val original = task()
        val repo = Repo(original)
        val images = Images().apply { importGate = CompletableDeferred() }
        var now = at - 1
        val mutations = TaskMutations(repo, Scheduler(), images, now = { now })
        val saving = async {
            assertFailsWith<ExpiredReminderException> {
                mutations.save(original.copy(repeatRule = RepeatRule.NONE), selectedImages = selection)
            }
        }
        runCurrent()
        assertTrue(images.importStarted)
        now = at
        images.importGate!!.complete(Unit)
        saving.await()
        assertEquals(original, repo.rows[1])
        assertEquals(listOf("new.jpg"), images.removed)
    }

    @Test fun elapsedDailyAndWeeklyAnchorsStillSaveAndSchedule() = runTest {
        val now = at + 1
        for (rule in listOf(RepeatRule.DAILY, RepeatRule.WEEKLY)) {
            val scheduler = Scheduler()
            val repo = Repo()
            val mutations = TaskMutations(repo, scheduler, now = { now })
            val result = mutations.save(Task(title = "Practice", reminderAt = at, repeatRule = rule, reminderTimeZoneId = "UTC"))
            val reminder = scheduler.synchronized.single()
            assertEquals(at, repo.rows.getValue(result.id).reminderAt)
            assertEquals(rule, reminder.repeatRule)
            assertTrue(requireNotNull(reminder.nextOccurrence(now)) > now)
        }
    }

    @Test fun receiversProceedDuringImportAndSavePreservesTheirCompletion() = runTest {
        val original = task()
        val repo = Repo(original)
        val images = Images().apply { importGate = CompletableDeferred() }
        val scheduler = Scheduler()
        val mutations = TaskMutations(repo, scheduler, images)
        val saving = async { mutations.save(original.copy(title = "Edited"), selectedImages = selection) }
        runCurrent()
        assertTrue(images.importStarted)
        var delivered = false
        val receiverWork = launch {
            mutations.restoreReminders()
            mutations.deliverReminder(1, at) { delivered = true }
            mutations.setCompleted(1, true)
        }
        try {
            runCurrent()
            assertTrue(receiverWork.isCompleted, "Receivers must not wait for the image provider")
            assertTrue(delivered)
            assertTrue(repo.rows.getValue(1).done)
        } finally { images.importGate!!.complete(Unit) }
        saving.await()
        assertTrue(repo.rows.getValue(1).done)
        assertEquals("Edited", repo.rows.getValue(1).title)
        assertEquals(listOf("old.jpg", "new.jpg"), repo.rows.getValue(1).imageNames)
        assertTrue(scheduler.synchronized.isEmpty(), "The completed task must not be rearmed")
    }

    @Test fun deletionDuringImportRejectsSaveAndRemovesStagedFiles() = runTest {
        val original = task()
        val repo = Repo(original)
        val images = Images().apply { importGate = CompletableDeferred() }
        val mutations = TaskMutations(repo, Scheduler(), images)
        val saving = async { runCatching { mutations.save(original, selectedImages = selection) } }
        runCurrent()
        val deleting = launch { mutations.delete(1) }
        try {
            runCurrent()
            assertTrue(deleting.isCompleted)
            assertNull(repo.rows[1])
        } finally { images.importGate!!.complete(Unit) }
        assertIs<IllegalArgumentException>(saving.await().exceptionOrNull())
        assertNull(repo.rows[1], "A stale form must not recreate the deleted task")
        assertEquals(setOf("old.jpg", "new.jpg"), images.removed.toSet())
    }

    @Test fun cancellingSaveWaitingForCommitLockRollsBackOnlyStagedFiles() = runTest {
        val original = task()
        val repo = Repo(original)
        val images = Images()
        val gate = CompletableDeferred<Unit>()
        val scheduler = Scheduler().apply { synchronizeGate = gate }
        val mutations = TaskMutations(repo, scheduler, images)
        val restoring = launch { mutations.restoreReminders() }
        runCurrent()
        val saving = launch { mutations.save(original, selectedImages = selection) }
        try {
            runCurrent()
            assertTrue(images.importStarted, "Staging must happen outside the commit lock")
            saving.cancelAndJoin()
            assertEquals(listOf("new.jpg"), images.removed)
            assertEquals(original, repo.rows[1])
        } finally { gate.complete(Unit) }
        restoring.join()
    }

    @Test fun saveCleanupDoesNotBlockReceiversOrAllowStaleAttachmentRestoration() = runTest {
        val original = task()
        val repo = Repo(original)
        val images = Images().apply { removeGate = CompletableDeferred() }
        val mutations = TaskMutations(repo, Scheduler(), images)
        val removing = launch { mutations.save(original.copy(imageNames = emptyList())) }
        runCurrent()
        assertTrue(images.removeStarted)
        var delivered = false
        val otherWork = launch {
            mutations.deliverReminder(1, at) { delivered = true }
            mutations.save(original.copy(title = "Stale form"))
        }
        try {
            runCurrent()
            assertTrue(otherWork.isCompleted, "File deletion must not hold the reminder lock")
            assertTrue(delivered)
            assertTrue(repo.rows.getValue(1).imageNames.isEmpty(), "Do not reattach a file pending deletion")
        } finally { images.removeGate!!.complete(Unit) }
        removing.join()
        assertEquals(listOf("old.jpg"), images.removed)
    }

    @Test fun deleteCleanupDoesNotBlockAnotherTasksReminder() = runTest {
        val repo = Repo(task(), task(2).copy(imageNames = emptyList()))
        val images = Images().apply { removeGate = CompletableDeferred() }
        val mutations = TaskMutations(repo, Scheduler(), images)
        val deleting = launch { mutations.delete(1) }
        runCurrent()
        assertTrue(images.removeStarted)
        var delivered = false
        val delivery = launch { mutations.deliverReminder(2, at) { delivered = true } }
        try {
            runCurrent()
            assertTrue(delivery.isCompleted)
            assertTrue(delivered)
            assertNull(repo.rows[1])
        } finally { images.removeGate!!.complete(Unit) }
        deleting.join()
    }

    @Test fun completionCannotInterleaveBetweenDeliveryValidationAndNotification() = runTest {
        val repo = Repo(task())
        val events = mutableListOf<String>()
        val gate = CompletableDeferred<Unit>()
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) {
                gate.await()
                events += "rearm"
            }
            override suspend fun cancelReminder(taskId: Long) { events += "cancel" }
        }
        val mutations = TaskMutations(repo, scheduler)
        val delivery = launch { mutations.deliverReminder(1, at) { events += "notify" } }
        runCurrent()
        val completing = launch { mutations.setCompleted(1, true) }
        try {
            runCurrent()
            assertFalse(completing.isCompleted)
            assertFalse(repo.rows.getValue(1).done)
        } finally { gate.complete(Unit) }
        delivery.join()
        completing.join()
        assertEquals(listOf("rearm", "notify", "cancel"), events)
        assertTrue(repo.rows.getValue(1).done)
    }

    private val selection = listOf(ImageSource("provider-image"))

    private class Images : ImageStore {
        var importGate: CompletableDeferred<Unit>? = null
        var removeGate: CompletableDeferred<Unit>? = null
        var importStarted = false
        var removeStarted = false
        val removed = mutableListOf<String>()
        override suspend fun importImages(sources: List<ImageSource>): List<String> {
            importStarted = true
            importGate?.await()
            return listOf("new.jpg")
        }
        override suspend fun removeImages(names: List<String>) {
            removeStarted = true
            removeGate?.await()
            removed += names
        }
    }

    private class Scheduler : ReminderScheduler {
        var synchronizeGate: CompletableDeferred<Unit>? = null
        var synchronized = emptyList<Reminder>()
        override suspend fun scheduleReminder(reminder: Reminder) = Unit
        override suspend fun cancelReminder(taskId: Long) = Unit
        override suspend fun synchronize(reminders: List<Reminder>) {
            synchronizeGate?.await()
            synchronized = reminders
        }
    }

    private class Repo(vararg tasks: Task) : TaskRepository {
        val rows = tasks.associateBy { requireNotNull(it.id) }.toMutableMap()
        override fun getAllTasks() = flowOf(rows.values.toList())
        override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
        override suspend fun getTaskById(id: Long) = rows[id]
        override suspend fun clearGroup(id: Long) = Unit
        override suspend fun deleteTask(id: Long) { rows.remove(id) }
        override suspend fun upsertTask(task: Task): Long {
            val id = task.id ?: (rows.keys.maxOrNull() ?: 0) + 1
            rows[id] = task.copy(id = id)
            return id
        }
    }
}
