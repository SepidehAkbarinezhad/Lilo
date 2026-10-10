package com.sepideh.lilo.note.domain.usecase

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.repository.NoteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class NoteMutations(
    private val repository: NoteRepository,
    private val imageStore: ImageStore,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    suspend fun save(note: Note, selectedImages: List<ImageSource>? = null): Note {
        require(note.title.isNotBlank())
        val original = if (note.id != 0L) requireNotNull(repository.getNoteById(note.id)) else null
        val timestamp = now()
        val imported = selectedImages?.let { imageStore.importImages(it) }
        val updated = note.copy(title = note.title.trim(), imageNames = note.imageNames + imported.orEmpty(),
            createdAt = original?.createdAt ?: timestamp, updatedAt = timestamp)
        val saved = try {
            val returnedId = repository.upsertNote(updated)
            updated.copy(id = if (updated.id == 0L) returnedId else updated.id)
        } catch (failure: Throwable) {
            // New files must not be orphaned when the database write fails or is cancelled.
            withContext(NonCancellable) { imported?.let { removeBestEffort(it) } }
            throw failure
        }
        removeBestEffort(original?.imageNames.orEmpty() - saved.imageNames.toSet())
        return saved
    }

    suspend fun delete(id: Long) {
        val note = repository.getNoteById(id) ?: return
        repository.deleteNote(id)
        removeBestEffort(note.imageNames)
    }

    private suspend fun removeBestEffort(names: List<String>) {
        if (names.isEmpty()) return
        try { imageStore.removeImages(names) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { /* Cleanup cannot undo a committed note mutation. */ }
    }
}
