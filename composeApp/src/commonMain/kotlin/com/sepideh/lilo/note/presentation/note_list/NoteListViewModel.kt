@file:OptIn(FlowPreview::class)

package com.sepideh.lilo.note.presentation.note_list

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.repository.CategoryRepository
import com.sepideh.lilo.category.domain.toPresentationList
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.presentation.note_list.model.NoteSortOrder
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteListViewModel(
    private val languageProvider: LanguageProvider,
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository,
) : BaseViewModel() {

    private val _categories: StateFlow<List<CategoryDomain>> =
        categoryRepository.getAllCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _notes = MutableStateFlow<List<Note>>(emptyList())

    private val _state = MutableStateFlow(NoteListState())

    private val _debouncedSearchQuery = _state
        .map { it.searchQuery }
        .debounce(300L)
        .distinctUntilChanged()

    val state = combine(
        _state,
        _notes,
        _categories,
        _debouncedSearchQuery,
        languageProvider.languageFlow
    ) { state, notes, categories, searchQuery, currentLanguage ->
        val updatedCategories: List<CategoryDomain> =
            listOf(CategoryDomain.categories[0]) + categories
        val validSelectedCategory = categories.find { it.id == state.selectedCategory }

        state.copy(
            notesResult = notes.let { noteList ->
                val filteredBasedOnCategory = if (validSelectedCategory != null) {
                    noteList.filter { note -> note.categoryId == validSelectedCategory.id }
                } else {
                    noteList
                }
                val filtered = filteredBasedOnCategory.filter { note ->
                    note.title.contains(searchQuery, ignoreCase = true) ||
                            note.content.contains(searchQuery, ignoreCase = true)
                }
                when (state.sortOrder) {
                    NoteSortOrder.Date -> filtered.sortedByDescending { it.updatedAt }
                    NoteSortOrder.Title -> filtered.sortedBy { it.title }
                }
            },
            categories = updatedCategories.toPresentationList(currentLanguage),
            selectedCategory = validSelectedCategory?.id
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), NoteListState())

    private var selectedNote: Note? = null

    init {
        loadNotes()
    }

    private fun loadNotes() {
        onAction(BaseAction.ShowLoading(true))
        viewModelScope.launch {
            noteRepository.getAllNotes()
                .collect { noteList ->
                    delay(500)
                    _notes.value = noteList
                    onAction(BaseAction.ShowLoading(false))
                }
        }
    }

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is NoteListAction.OnSortOrderChanged -> {
                _state.update { it.copy(sortOrder = action.sortOrder) }
            }

            is NoteListAction.OnCategorySelected -> {
                _state.update { it.copy(selectedCategory = action.id) }
            }

            is NoteListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }

            is NoteListAction.OnSearchToggle -> {
                _state.update { it.copy(isSearchVisible = action.open) }
            }

            is NoteListAction.OnDeleteNoteIcon -> {
                selectedNote = action.note
                _state.update { it.copy(isDeleteDialogOpen = true) }
            }

            is NoteListAction.OnDismissDeleteDialog -> {
                _state.update { it.copy(isDeleteDialogOpen = false) }
            }

            is NoteListAction.OnDeleteNoteConfirm -> {
                onAction(BaseAction.ShowLoading(true))
                selectedNote?.let { note ->
                    viewModelScope.launch {
                        delay(500)
                        withContext(Dispatchers.IO) {
                            noteRepository.deleteNote(id = note.id)
                        }
                        onAction(BaseAction.ShowLoading(false))
                    }
                }
            }
        }
    }

    override fun onResetState() {}
}