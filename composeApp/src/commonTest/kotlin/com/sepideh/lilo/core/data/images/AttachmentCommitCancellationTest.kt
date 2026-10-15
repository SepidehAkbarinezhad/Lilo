package com.sepideh.lilo.core.data.images

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AttachmentCommitCancellationTest {
    @Test fun cancellingTaskSaveAfterCommitRetainsNewFilesAndFinishesScheduling() = runTest {
        val commit = CommitBoundary(StandardTestDispatcher(testScheduler))
        val images = Images()
        var row = Task(id = 1, title = "Before", imageNames = listOf("old.jpg"))
        val repo = object : TaskRepository {
            override fun getAllTasks() = flowOf(listOf(row))
            override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
            override suspend fun getTaskById(id: Long) = row
            override suspend fun clearGroup(id: Long) = Unit
            override suspend fun deleteTask(id: Long) = Unit
            override suspend fun upsertTask(task: Task): Long = commit.write { row = task }
        }
        var synchronized = false
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) = Unit
            override suspend fun cancelReminder(taskId: Long) = Unit
            override suspend fun synchronize(reminders: List<Reminder>) { synchronized = true }
        }
        val saving = launch {
            TaskMutations(repo, scheduler, images).save(row.copy(imageNames = emptyList()),
                selectedImages = listOf(ImageSource("selection")))
        }
        commit.committed.await()
        saving.cancel()
        commit.returnFromDatabase.complete(Unit)
        saving.join()
        assertEquals(listOf("new.jpg"), row.imageNames)
        assertEquals(listOf("old.jpg"), images.removed)
        assertTrue(synchronized)
    }

    @Test fun cancellingNoteSaveAfterCommitRetainsNewFilesAndCleansReplacedFiles() = runTest {
        val commit = CommitBoundary(StandardTestDispatcher(testScheduler))
        val images = Images()
        var row = Note(id = 1, title = "Before", imageNames = listOf("old.jpg"))
        val repo = object : NoteRepository {
            override fun getAllNotes() = flowOf(listOf(row))
            override suspend fun getNoteById(id: Long) = row
            override suspend fun deleteNote(id: Long) = Unit
            override suspend fun upsertNote(note: Note): Long = commit.write { row = note }
        }
        val saving = launch {
            NoteMutations(repo, images).save(row.copy(imageNames = emptyList()), listOf(ImageSource("selection")))
        }
        commit.committed.await()
        saving.cancel()
        commit.returnFromDatabase.complete(Unit)
        saving.join()
        assertEquals(listOf("new.jpg"), row.imageNames)
        assertEquals(listOf("old.jpg"), images.removed)
    }

    // Model a DB commit finishing on its own dispatcher before a cancelled caller receives the result.
    private class CommitBoundary(private val dispatcher: CoroutineDispatcher) {
        val committed = CompletableDeferred<Unit>()
        val returnFromDatabase = CompletableDeferred<Unit>()
        suspend fun write(persist: () -> Unit): Long = withContext(dispatcher) {
            withContext(NonCancellable) {
                persist()
                committed.complete(Unit)
                returnFromDatabase.await()
                -1L
            }
        }
    }

    private class Images : ImageStore {
        val removed = mutableListOf<String>()
        override suspend fun importImages(sources: List<ImageSource>) = listOf("new.jpg")
        override suspend fun removeImages(names: List<String>) { removed.addAll(names) }
    }
}
