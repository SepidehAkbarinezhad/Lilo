package com.sepideh.lilo.note.presentation

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.*
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.repository.*
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.note.presentation.detail.*
import com.sepideh.lilo.note.domain.model.NoteGroup
import com.sepideh.lilo.core.domain.images.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NoteGroupSheetTest {
    private val store = ViewModelStore()
    @BeforeTest fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun cleanup() { store.clear(); Dispatchers.resetMain() }

    @Test fun addFailureKeepsManagementAndSuccessfulRetrySelectsAndCloses() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(NoteDetailAction.Load(1))
        runCurrent()
        model.onAction(NoteDetailAction.OpenGroups)
        model.onAction(NoteDetailAction.ManageGroups)
        groups.failure = DuplicateNameException()
        model.onAction(NoteDetailAction.AddGroup("New"))
        runCurrent()
        assertTrue(model.state.value.duplicateGroupName)
        assertTrue(model.state.value.groupManagementOpen)
        assertTrue(model.state.value.groupDialogOpen)
        assertEquals(7L, model.state.value.note.groupId)
        groups.failure = null
        model.onAction(NoteDetailAction.AddGroup("New"))
        runCurrent()
        assertEquals(100L, model.state.value.note.groupId)
        assertEquals(100L, model.state.value.draftGroupId)
        assertFalse(model.state.value.groupDialogOpen)
        assertFalse(model.state.value.groupManagementOpen)
        model.onAction(NoteDetailAction.OpenGroups)
        runCurrent()
        assertEquals(100L, model.state.value.draftGroupId)
        assertFalse(model.state.value.groupManagementOpen)
    }

    @Test fun renameSelectedOrOtherGroupRetainsSelectionAndManagement() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(NoteDetailAction.Load(1))
        runCurrent()
        model.onAction(NoteDetailAction.OpenGroups)
        model.onAction(NoteDetailAction.ManageGroups)
        for (id in listOf(7L, 42L)) {
            model.onAction(NoteDetailAction.RenameGroup(id, "ZZZ renamed"))
            runCurrent()
            assertTrue(model.state.value.groupDialogOpen)
            assertTrue(model.state.value.groupManagementOpen)
            assertEquals(7L, model.state.value.note.groupId)
            assertEquals(7L, model.state.value.draftGroupId)
            val saved = groups.getGroupById(id)!!
            assertTrue(saved.titleEn == "ZZZ renamed" || saved.titleFa == "ZZZ renamed")
        }
    }

    @Test fun deletionKeepsManagementAndClearsOnlyDeletedSelection() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(NoteDetailAction.Load(1))
        runCurrent()
        model.onAction(NoteDetailAction.OpenGroups)
        model.onAction(NoteDetailAction.ManageGroups)
        model.onAction(NoteDetailAction.DeleteGroup(42))
        runCurrent()
        assertEquals(7L, model.state.value.note.groupId)
        assertEquals(7L, model.state.value.draftGroupId)
        model.onAction(NoteDetailAction.DeleteGroup(7))
        runCurrent()
        assertEquals(null, model.state.value.note.groupId)
        assertNull(model.state.value.draftGroupId)
        assertTrue(model.state.value.groupManagementOpen)
        assertTrue(model.state.value.groupDialogOpen)
    }

    @Test fun closeAndDismissExitEntireSheetWithoutConfirmingUncommittedSelection() = runTest {
        val model = model(Groups())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(NoteDetailAction.Load(1))
        runCurrent()
        for (close in listOf(NoteDetailAction.CloseManagement, NoteDetailAction.DismissGroups)) {
            model.onAction(NoteDetailAction.OpenGroups)
            model.onAction(NoteDetailAction.DraftGroup(42))
            model.onAction(NoteDetailAction.ManageGroups)
            model.onAction(close)
            runCurrent()
            assertFalse(model.state.value.groupDialogOpen)
            assertFalse(model.state.value.groupManagementOpen)
            assertEquals(7L, model.state.value.note.groupId)
        }
        model.onAction(NoteDetailAction.OpenGroups)
        runCurrent()
        assertEquals(7L, model.state.value.draftGroupId)
    }

    @Test fun pendingDeleteBlocksCloseAndFailureAllowsRetry() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(NoteDetailAction.Load(1))
        runCurrent()
        model.onAction(NoteDetailAction.OpenGroups)
        model.onAction(NoteDetailAction.ManageGroups)
        groups.gate = CompletableDeferred()
        groups.failure = IllegalStateException("Storage failure")
        model.onAction(NoteDetailAction.DeleteGroup(7))
        runCurrent()
        model.onAction(NoteDetailAction.CloseManagement)
        model.onAction(NoteDetailAction.DismissGroups)
        runCurrent()
        assertTrue(model.state.value.isGroupWorking)
        assertTrue(model.state.value.groupManagementOpen)
        assertTrue(model.state.value.groupDialogOpen)
        groups.gate!!.complete(Unit)
        runCurrent()
        assertFalse(model.state.value.isGroupWorking)
        assertTrue(model.state.value.hasError)
        assertEquals(7L, model.state.value.note.groupId)
        groups.failure = null
        model.onAction(NoteDetailAction.DeleteGroup(7))
        runCurrent()
        assertEquals(null, model.state.value.note.groupId)
        assertFalse(model.state.value.hasError)
        assertTrue(model.state.value.groupManagementOpen)
    }

    private fun model(groups: Groups): NoteDetailViewModel {
        val language = LanguageProvider(UserPreferencesManager(object : UserPreferencesRepository {
            override val userPreferences = flowOf(UserPreferences())
            override suspend fun updateTheme(theme: AppTheme) = Unit
            override suspend fun updateLanguage(language: AppLanguage) = Unit
            override suspend fun updateNotifications(enabled: Boolean) = Unit
        }))
        val repository = object : NoteRepository {
            override fun getAllNotes() = flowOf(emptyList<Note>())
            override suspend fun getNoteById(id: Long) = Note(id = id, title = "Original", groupId = 7)
            override suspend fun upsertNote(note: Note): Long = error("Sheet must not save note")
            override suspend fun deleteNote(id: Long) = Unit
        }
        val images = object : ImageStore {
            override suspend fun importImages(sources: List<ImageSource>) = emptyList<String>()
            override suspend fun removeImages(names: List<String>) = Unit
        }
        return NoteDetailViewModel(repository, groups, language, NoteMutations(repository, images))
            .also { store.put("editor", it) }
    }

    private class Groups : NoteGroupRepository {
        private val rows = MutableStateFlow(listOf(NoteGroup(id = 7, titleEn = "Original", titleFa = "Original"),
            NoteGroup(id = 42, titleEn = "Other", titleFa = "Other")))
        var failure: Exception? = null
        var gate: CompletableDeferred<Unit>? = null
        override fun getAllGroups() = rows.asStateFlow()
        override suspend fun getGroupById(id: Long) = rows.value.firstOrNull { it.id == id }
        override suspend fun upsertGroup(group: NoteGroup): Long {
            failure?.let { throw it }
            val saved = group.copy(id = group.id.takeIf { it != 0L } ?: 100)
            rows.value = (rows.value.filterNot { it.id == saved.id } + saved).sortedBy { it.titleFa }
            return saved.id
        }
        override suspend fun deleteGroup(id: Long) {
            gate?.await()
            failure?.let { throw it }
            rows.value = rows.value.filterNot { it.id == id }
        }
    }
}
