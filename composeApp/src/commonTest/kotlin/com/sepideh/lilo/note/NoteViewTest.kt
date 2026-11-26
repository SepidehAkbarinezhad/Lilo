package com.sepideh.lilo.note

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.app.navigation.routeForAdding
import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.core.presentation.images.AttachmentPreview
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.model.NoteGroup
import com.sepideh.lilo.note.domain.repository.NoteGroupRepository
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.note.presentation.toGroupOptions
import com.sepideh.lilo.note.presentation.detail.NoteDetailAction
import com.sepideh.lilo.note.presentation.detail.NoteDetailViewModel
import com.sepideh.lilo.note.presentation.view.NoteViewAction
import com.sepideh.lilo.note.presentation.view.NoteViewViewModel
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewTest {
    private val store = ViewModelStore()

    @BeforeTest
    fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }

    @AfterTest
    fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test
    fun viewingPreservesFullContentAndNavigatesWithoutWriting() = runTest {
        val note = Note(
            id = 17,
            title = "A long title — یک عنوان طولانی".repeat(20),
            content = "Full content\nمتن کامل\n".repeat(100),
            imageNames = listOf("one.jpg", "two.jpg"),
        )
        val repository = FakeNotes(note)
        val model = viewer(repository)
        assertFalse(model.state.value.canEdit)
        model.onAction(NoteViewAction.Edit)
        assertNull(withTimeoutOrNull(1) { model.baseOneTimeEvents.first() })
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertEquals(note, model.state.value.note)
        assertTrue(model.state.value.canEdit)

        model.onAction(NoteViewAction.Edit)
        assertEquals(AppRoutes.Notes.Detail(17), model.baseOneTimeEvents.first().route)
        model.onAction(NoteViewAction.Back)
        assertTrue(model.baseOneTimeEvents.first().navigateBack)
        assertEquals(0, repository.writes)
    }

    @Test
    fun savingExistingEditorReturnsAndReloadsUpdatedTextAttachmentsAndMetadata() = runTest {
        val repository = FakeNotes(Note(
            id = 17, title = "Original", imageNames = listOf("old.jpg"),
            groupId = 7, createdAt = 10, updatedAt = 20,
        ))
        val groups = FakeGroups(listOf(NoteGroup(id = 7, titleEn = "Work"), NoteGroup(id = 42, titleEn = "Ideas")))
        val model = viewer(repository, groups)
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertEquals(7L, model.state.value.group?.id)
        val editor = editor(repository, groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { editor.state.collect {} }
        editor.onAction(NoteDetailAction.Load(17))
        runCurrent()
        editor.onAction(NoteDetailAction.Title("Updated title"))
        editor.onAction(NoteDetailAction.Content("Updated content\nمتن جدید"))
        editor.onAction(NoteDetailAction.RemoveImage(AttachmentPreview("old.jpg", stored = true)))
        editor.onAction(NoteDetailAction.Images(listOf(ImageSource("new.jpg"))))
        editor.onAction(NoteDetailAction.DraftGroup(42))
        editor.onAction(NoteDetailAction.ConfirmGroup)
        runCurrent()
        editor.onAction(NoteDetailAction.Save)
        runCurrent()
        assertTrue(editor.baseOneTimeEvents.first().navigateBack)
        assertEquals(1, repository.writes)

        // Mirrors the detail destination's ON_RESUME after the editor pops.
        model.onAction(NoteViewAction.Load(17))
        assertFalse(model.state.value.canEdit)
        runCurrent()
        assertEquals("Updated title", model.state.value.note?.title)
        assertEquals("Updated content\nمتن جدید", model.state.value.note?.content)
        assertEquals(listOf("new.jpg"), model.state.value.note?.imageNames)
        assertEquals(42L, model.state.value.group?.id)
        assertEquals(50L, model.state.value.note?.lastModifiedAt)
        assertTrue(model.state.value.canEdit)
        assertNull(withTimeoutOrNull(1) { model.baseOneTimeEvents.first() })
    }

    @Test
    fun addNoteStillUsesExistingFormAndPopsAfterSaving() = runTest {
        assertEquals(AppRoutes.Notes.Detail(null), LiloFeature.NOTES.routeForAdding(null))
        val repository = FakeNotes(null)
        val editor = editor(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { editor.state.collect {} }
        editor.onAction(NoteDetailAction.Load(null))
        runCurrent()
        editor.onAction(NoteDetailAction.Title("New note"))
        runCurrent()
        editor.onAction(NoteDetailAction.Save)
        runCurrent()
        assertEquals("New note", repository.note?.title)
        assertEquals(1, repository.writes)
        assertTrue(editor.baseOneTimeEvents.first().navigateBack)
    }

    @Test
    fun loadFailureCanBeRetried() = runTest {
        val repository = FakeNotes(Note(id = 17, title = "Note"))
        repository.failure = IllegalStateException("Database unavailable")
        val model = viewer(repository)
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertTrue(model.state.value.hasError)
        assertFalse(model.state.value.canEdit)
        assertFalse(model.state.value.noteMissing)
        repository.failure = null
        model.onAction(NoteViewAction.Retry)
        runCurrent()
        assertFalse(model.state.value.hasError)
        assertEquals(repository.note, model.state.value.note)
        assertTrue(model.state.value.canEdit)
    }

    @Test
    fun deletedNoteClearsStaleContentAndCannotOpenEditor() = runTest {
        val repository = FakeNotes(Note(id = 17, title = "Note"))
        val model = viewer(repository)
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        repository.note = null
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertTrue(model.state.value.noteMissing)
        assertNull(model.state.value.note)
        assertFalse(model.state.value.canEdit)
        model.onAction(NoteViewAction.Edit)
        assertNull(withTimeoutOrNull(1) { model.baseOneTimeEvents.first() })
        model.onAction(NoteViewAction.Back)
        assertTrue(model.baseOneTimeEvents.first().navigateBack)
    }

    @Test
    fun newLoadCancelsPreviousReadWithoutDisplayingAnError() = runTest {
        val repository = FakeNotes(Note(id = 17, title = "Note"))
        val pendingRead = CompletableDeferred<Unit>()
        repository.beforeRead = { pendingRead.await() }
        val model = viewer(repository)
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        repository.beforeRead = {}
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertEquals(1, repository.cancelledReads)
        assertFalse(model.state.value.hasError)
        assertTrue(model.state.value.canEdit)
        assertEquals(repository.note, model.state.value.note)
    }

    @Test
    fun groupUsesPersistedIdAndExistingLocalizedLabels() = runTest {
        val selected = NoteGroup(id = 42, titleEn = "Ideas", titleFa = "ایده‌ها")
        val groups = FakeGroups(listOf(NoteGroup(id = 1, titleEn = "Other"), selected))
        val model = viewer(FakeNotes(Note(id = 17, title = "Note", groupId = 42)), groups)
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertEquals(listOf(42L), groups.requestedIds)
        assertEquals(selected, model.state.value.group)
        assertEquals("Ideas", listOfNotNull(model.state.value.group).toGroupOptions(AppLanguage.EN).single().label)
        assertEquals("ایده‌ها", listOfNotNull(model.state.value.group).toGroupOptions(AppLanguage.FA).single().label)

        // User-created groups may only have a name in one language.
        groups.groups = listOf(selected.copy(titleEn = ""))
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertEquals("ایده‌ها", listOfNotNull(model.state.value.group).toGroupOptions(AppLanguage.EN).single().label)
    }

    @Test
    fun removedOrUnassignedGroupDoesNotLeaveAStaleChip() = runTest {
        val repository = FakeNotes(Note(id = 17, title = "Note", groupId = 42))
        val groups = FakeGroups(listOf(NoteGroup(id = 42, titleEn = "Ideas")))
        val model = viewer(repository, groups)
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertEquals(42L, model.state.value.group?.id)

        groups.groups = emptyList()
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertNull(model.state.value.group)
        assertEquals(repository.note, model.state.value.note)
        assertTrue(model.state.value.canEdit)

        repository.note = requireNotNull(repository.note).copy(groupId = null)
        groups.requestedIds.clear()
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertNull(model.state.value.group)
        assertTrue(groups.requestedIds.isEmpty())
    }

    @Test
    fun groupLoadFailureCanBeRetried() = runTest {
        val groups = FakeGroups(listOf(NoteGroup(id = 42, titleEn = "Ideas")))
        val model = viewer(FakeNotes(Note(id = 17, title = "Note", groupId = 42)), groups)
        groups.failure = IllegalStateException("Database unavailable")
        model.onAction(NoteViewAction.Load(17))
        runCurrent()
        assertTrue(model.state.value.hasError)
        assertFalse(model.state.value.canEdit)

        groups.failure = null
        model.onAction(NoteViewAction.Retry)
        runCurrent()
        assertFalse(model.state.value.hasError)
        assertTrue(model.state.value.canEdit)
        assertEquals(42L, model.state.value.group?.id)
    }

    private fun viewer(repository: NoteRepository, groups: NoteGroupRepository = FakeGroups()) = NoteViewViewModel(repository, groups).also {
        store.put("viewer", it)
    }

    private fun editor(repository: NoteRepository, groups: NoteGroupRepository = FakeGroups()): NoteDetailViewModel {
        val images = object : ImageStore {
            override suspend fun importImages(sources: List<ImageSource>) = sources.map { it.location }
            override suspend fun removeImages(names: List<String>) = Unit
        }
        val language = LanguageProvider(UserPreferencesManager(object : UserPreferencesRepository {
            override val userPreferences = MutableStateFlow(UserPreferences(language = AppLanguage.EN))
            override suspend fun updateTheme(theme: AppTheme) = Unit
            override suspend fun updateLanguage(language: AppLanguage) = Unit
            override suspend fun updateNotifications(enabled: Boolean) = Unit
        }))
        return NoteDetailViewModel(repository, groups, language, NoteMutations(repository, images, now = { 50 })).also {
            store.put("editor", it)
        }
    }

    private class FakeGroups(var groups: List<NoteGroup> = emptyList()) : NoteGroupRepository {
        val requestedIds = mutableListOf<Long>()
        var failure: Exception? = null
        override fun getAllGroups() = flowOf(groups)
        override suspend fun getGroupById(id: Long): NoteGroup? {
            requestedIds += id
            failure?.let { throw it }
            return groups.firstOrNull { it.id == id }
        }
        override suspend fun upsertGroup(group: NoteGroup): Long = error("Unexpected group write")
        override suspend fun deleteGroup(id: Long) = error("Unexpected group delete")
    }

    private class FakeNotes(var note: Note?) : NoteRepository {
        var failure: Exception? = null
        var beforeRead: suspend () -> Unit = {}
        var cancelledReads = 0
        var writes = 0
        override fun getAllNotes() = flowOf(listOfNotNull(note))
        override suspend fun getNoteById(id: Long): Note? {
            try {
                beforeRead()
                failure?.let { throw it }
                return note?.takeIf { it.id == id }
            } catch (cancelled: CancellationException) {
                cancelledReads++
                throw cancelled
            }
        }
        override suspend fun upsertNote(note: Note): Long {
            writes++
            this.note = note.copy(id = note.id.takeIf { it != 0L } ?: 1L)
            return requireNotNull(this.note).id
        }
        override suspend fun deleteNote(id: Long) = error("Unexpected note delete")
    }
}
