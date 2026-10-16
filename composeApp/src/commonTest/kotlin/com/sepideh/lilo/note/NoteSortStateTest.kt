package com.sepideh.lilo.note

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.images.*
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.note.domain.model.*
import com.sepideh.lilo.note.domain.repository.*
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.note.presentation.list.*
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NoteSortStateTest {
    private val store = ViewModelStore()
    @BeforeTest fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test fun applyingAndDismissingSortingPreserveSearchAndCategory() = runTest {
        val repository = object : NoteRepository {
            override fun getAllNotes() = flowOf(listOf(
                Note(id = 1, title = "Zebra", content = "match", groupId = 7, updatedAt = 30),
                Note(id = 2, title = "Apple", content = "match", groupId = 7, updatedAt = 20),
                Note(id = 3, title = "Other group", content = "match", groupId = 8, updatedAt = 40),
                Note(id = 4, title = "Other search", groupId = 7, updatedAt = 50)))
            override suspend fun getNoteById(id: Long): Note? = error("Unexpected read")
            override suspend fun upsertNote(note: Note): Long = error("Unexpected write")
            override suspend fun deleteNote(id: Long) = error("Unexpected delete")
        }
        val groups = object : NoteGroupRepository {
            override fun getAllGroups() = flowOf(listOf(NoteGroup(7, "Work"), NoteGroup(8, "Home")))
            override suspend fun getGroupById(id: Long): NoteGroup? = error("Unexpected read")
            override suspend fun upsertGroup(group: NoteGroup): Long = error("Unexpected write")
            override suspend fun deleteGroup(id: Long) = error("Unexpected delete")
        }
        val images = object : ImageStore {
            override suspend fun importImages(sources: List<ImageSource>): List<String> = error("Unexpected import")
            override suspend fun removeImages(names: List<String>) = error("Unexpected removal")
        }
        val language = LanguageProvider(UserPreferencesManager(object : UserPreferencesRepository {
            override val userPreferences = flowOf(UserPreferences())
            override suspend fun updateTheme(theme: AppTheme) {}
            override suspend fun updateLanguage(language: AppLanguage) {}
            override suspend fun updateNotifications(enabled: Boolean) {}
        }))
        val model = NoteListViewModel(language, repository, groups, NoteMutations(repository, images))
        store.put("notes", model)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(NoteListAction.Search("match"))
        model.onAction(NoteListAction.SelectGroup(7))
        advanceTimeBy(251)
        runCurrent()
        assertEquals(NoteSortOrder.Date, model.state.value.sortOrder)
        assertEquals(listOf(1L, 2L), model.state.value.notesResult.map { it.id })

        model.onAction(NoteListAction.OpenSort)
        model.onAction(NoteListAction.DraftSort(NoteSortOrder.Title))
        runCurrent()
        assertEquals(listOf(1L, 2L), model.state.value.notesResult.map { it.id })
        model.onAction(NoteListAction.DismissSort)
        runCurrent()
        assertEquals(NoteSortOrder.Date, model.state.value.draftSort)
        assertFalse(model.state.value.isSortSheetOpen)

        model.onAction(NoteListAction.OpenSort)
        model.onAction(NoteListAction.DraftSort(NoteSortOrder.Title))
        model.onAction(NoteListAction.ApplySort)
        runCurrent()
        assertEquals(listOf(2L, 1L), model.state.value.notesResult.map { it.id })
        assertEquals("match", model.state.value.searchQuery)
        assertEquals(7L, model.state.value.selectedGroup)
        assertFalse(model.state.value.isSortSheetOpen)
    }
}
