@file:OptIn(FlowPreview::class)

package com.sepideh.lilo.note.presentation.list

import com.sepideh.lilo.category.domain.repository.CategoryRepository
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import kotlinx.coroutines.FlowPreview

class NoteListViewModel(
    private val languageProvider: LanguageProvider,
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository,
) : BaseViewModel() {


    override fun onResetState() {}
}