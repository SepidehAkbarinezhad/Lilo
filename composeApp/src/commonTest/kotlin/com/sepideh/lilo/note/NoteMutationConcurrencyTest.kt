package com.sepideh.lilo.note

import com.sepideh.lilo.core.domain.images.*
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NoteMutationConcurrencyTest {
    @Test fun deleteWaitsForSaveAndCleansTheCommittedAttachmentSet() = runTest {
        var row: Note? = Note(id = 1, title = "Original", imageNames = listOf("old.jpg"))
        val importing = CompletableDeferred<Unit>()
        val finishImport = CompletableDeferred<Unit>()
        val removed = mutableListOf<String>()
        val repository = object : NoteRepository {
            override fun getAllNotes() = flowOf(listOfNotNull(row))
            override suspend fun getNoteById(id: Long) = row
            override suspend fun upsertNote(note: Note): Long { row = note; return -1 }
            override suspend fun deleteNote(id: Long) { row = null }
        }
        val images = object : ImageStore {
            override suspend fun importImages(sources: List<ImageSource>): List<String> {
                importing.complete(Unit)
                finishImport.await()
                return listOf("new.jpg")
            }
            override suspend fun removeImages(names: List<String>) { removed.addAll(names) }
        }
        val mutations = NoteMutations(repository, images)
        val save = launch { mutations.save(requireNotNull(row), listOf(ImageSource("selected"))) }
        importing.await()
        val delete = launch { mutations.delete(1) }
        runCurrent()
        assertNotNull(row)
        finishImport.complete(Unit)
        save.join(); delete.join()
        assertNull(row)
        assertEquals(setOf("old.jpg", "new.jpg"), removed.toSet())
    }

    @Test fun cancellationAtDeleteCommitStillFinishesAttachmentCleanup() = runTest {
        var row: Note? = Note(id = 1, title = "Original", imageNames = listOf("saved.jpg"))
        val committed = CompletableDeferred<Unit>()
        val returnFromDatabase = CompletableDeferred<Unit>()
        val removed = mutableListOf<String>()
        val repository = object : NoteRepository {
            override fun getAllNotes() = flowOf(listOfNotNull(row))
            override suspend fun getNoteById(id: Long) = row
            override suspend fun upsertNote(note: Note): Long = error("Not used")
            override suspend fun deleteNote(id: Long) {
                row = null
                committed.complete(Unit)
                returnFromDatabase.await()
            }
        }
        val images = object : ImageStore {
            override suspend fun importImages(sources: List<ImageSource>): List<String> = error("Not used")
            override suspend fun removeImages(names: List<String>) { removed.addAll(names) }
        }
        val deleting = launch { NoteMutations(repository, images).delete(1) }
        committed.await()
        deleting.cancel()
        returnFromDatabase.complete(Unit)
        deleting.join()
        assertNull(row)
        assertEquals(listOf("saved.jpg"), removed)
    }
}
