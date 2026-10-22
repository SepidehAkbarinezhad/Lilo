package com.sepideh.lilo.task.domain

import com.sepideh.lilo.core.domain.images.*
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.flowOf
import kotlin.test.*

class TaskAttachmentSaveTest {
    @Test fun savedAttachmentsAreRetainedAndRemovedFilesAreCleanedAfterCommit() = runBlocking {
        val repo = Repo(Task(id=1,title="Task",imageNames=listOf("keep.jpg","remove.jpg")))
        val images = Images()
        val result = TaskMutations(repo,Scheduler(),images).save(repo.task!!.copy(imageNames=listOf("keep.jpg")),selectedImages=listOf(ImageSource("selected")))
        assertEquals(listOf("keep.jpg","new.jpg"), result.imageNames)
        assertEquals(result.imageNames, repo.task!!.imageNames)
        assertEquals(listOf("remove.jpg"), images.removed)
    }
    @Test fun persistenceFailureRemovesOnlyImportedFiles() = runBlocking {
        val repo = Repo(Task(id=1,title="Task",imageNames=listOf("keep.jpg")),fail=true)
        val images = Images()
        assertFailsWith<IllegalStateException> { TaskMutations(repo,Scheduler(),images).save(repo.task!!,selectedImages=listOf(ImageSource("selected"))) }
        assertEquals(listOf("new.jpg"),images.removed)
        assertEquals(listOf("keep.jpg"),repo.task!!.imageNames)
    }
    @Test fun schedulingCancellationCannotDeleteImagesOfCommittedNewTask() = runBlocking {
        val repo = Repo()
        val images = Images()
        assertFailsWith<CancellationException> { TaskMutations(repo,Scheduler(cancel=true),images).save(Task(title="New"),scheduleReminder=false,selectedImages=listOf(ImageSource("selected"))) }
        assertEquals(listOf("new.jpg"),repo.task!!.imageNames)
        assertTrue(images.removed.isEmpty())
    }
    @Test fun alarmFailureCannotPreventDeletionOrLeaveItsImagesBehind() = runBlocking {
        val repo = Repo(Task(id=1,title="Task",imageNames=listOf("saved.jpg")))
        val images = Images()
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) = Unit
            override suspend fun cancelReminder(taskId: Long): Unit = error("Alarm service unavailable")
        }
        assertFailsWith<IllegalStateException> { TaskMutations(repo,scheduler,images).delete(1) }
        assertNull(repo.task)
        assertEquals(listOf("saved.jpg"),images.removed)
    }

    @Test fun databaseDeleteFailureLeavesImagesAndAlarmsIntact() = runBlocking {
        val repo = Repo(Task(id=1,title="Task",imageNames=listOf("saved.jpg")),fail=true)
        val images = Images()
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) = Unit
            override suspend fun cancelReminder(taskId: Long): Unit = error("Must not cancel before commit")
        }
        assertFailsWith<IllegalStateException> { TaskMutations(repo,scheduler,images).delete(1) }
        assertNotNull(repo.task)
        assertTrue(images.removed.isEmpty())
    }

    private class Images : ImageStore {
        val removed=mutableListOf<String>()
        override suspend fun importImages(sources:List<ImageSource>) = listOf("new.jpg")
        override suspend fun removeImages(names:List<String>) { removed.addAll(names) }
    }
    private class Scheduler(val cancel:Boolean=false) : ReminderScheduler {
        override suspend fun scheduleReminder(reminder:Reminder) {}
        override suspend fun cancelReminder(taskId:Long) { if(cancel) throw CancellationException() }
    }
    private class Repo(var task:Task?=null,val fail:Boolean=false) : TaskRepository {
        override fun getAllTasks()=flowOf(listOfNotNull(task))
        override fun getTasksByFilter(done:Boolean?,priority:List<Int>)=getAllTasks()
        override suspend fun getTaskById(id:Long)=task?.takeIf { it.id==id }
        override suspend fun clearGroup(id:Long) {}
        override suspend fun deleteTask(id:Long) { check(!fail); task=null }
        override suspend fun upsertTask(task:Task):Long {
            check(!fail)
            this.task=task.copy(id=task.id?:1L)
            return if(task.id==null) 1L else -1L
        }
    }
}
