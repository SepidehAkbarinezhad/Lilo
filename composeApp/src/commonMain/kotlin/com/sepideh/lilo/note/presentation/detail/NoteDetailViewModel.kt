package com.sepideh.lilo.note.presentation.detail

import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.model.NoteGroup
import com.sepideh.lilo.note.domain.repository.NoteGroupRepository
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.note.presentation.toGroupOptions
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lilo.composeapp.generated.resources.*

@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailViewModel(
    private val noteRepository: NoteRepository,
    private val groupRepository: NoteGroupRepository,
    private val languageProvider: LanguageProvider,
    private val mutations: NoteMutations,
) : BaseViewModel() {
    private val local = MutableStateFlow(NoteDetailState())
    private val refresh = MutableStateFlow(0)
    private var initialized = false
    private var requestedId: Long? = null
    private var original = Note()
    private val groups = refresh.flatMapLatest {
        groupRepository.getAllGroups().catch { local.update { ui -> ui.copy(hasError = true) }; emit(emptyList()) }
    }
    val state = combine(local, groups, languageProvider.languageFlow) { ui, groups, language ->
        ui.copy(groups = groups.toGroupOptions(language))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteDetailState())

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is NoteDetailAction.Load -> if (!initialized) { initialized = true; requestedId = action.id; load() }
            is NoteDetailAction.Title -> if (state.value.fieldsEnabled) local.update { it.copy(note = it.note.copy(title = action.value), titleError = ValidationStatus()) }
            is NoteDetailAction.Content -> if (state.value.fieldsEnabled) local.update { it.copy(note = it.note.copy(content = action.value)) }
            is NoteDetailAction.Images -> if (state.value.fieldsEnabled) {
                try { local.update { it.copy(selectedImages = com.sepideh.lilo.core.presentation.images.appendImageSelection(it.selectedImages, action.sources, it.note.imageNames.count { name -> name !in it.removedImageNames })) } }
                catch (_: IllegalArgumentException) { local.update { it.copy(hasError = true) } }
            }
            is NoteDetailAction.RemoveImage -> if (state.value.fieldsEnabled) local.update {
                if (action.image.stored) it.copy(removedImageNames = it.removedImageNames + action.image.location)
                else it.copy(selectedImages = it.selectedImages.orEmpty().filterNot { source -> source.location == action.image.location })
            }
            NoteDetailAction.ClearImages -> if (state.value.fieldsEnabled) local.update { it.copy(selectedImages = emptyList(), removedImageNames = it.note.imageNames.toSet()) }
            NoteDetailAction.ImageError -> local.update { it.copy(hasError = true) }
            NoteDetailAction.Save -> save()
            NoteDetailAction.Back -> if (!local.value.isSaving && !local.value.isGroupWorking) {
                if (local.value.note != original || local.value.selectedImages != null || local.value.removedImageNames.isNotEmpty()) local.update { it.copy(confirmDiscard = true) }
                else super.onAction(BaseAction.OnNavigateTo(null))
            }
            NoteDetailAction.Discard -> super.onAction(BaseAction.OnNavigateTo(null))
            NoteDetailAction.KeepEditing -> local.update { it.copy(confirmDiscard = false) }
            NoteDetailAction.OpenGroups -> if (state.value.fieldsEnabled) local.update { it.copy(groupDialogOpen = true, draftGroupId = it.note.groupId?.takeIf { id -> state.value.groups.any { it.id == id } }) }
            NoteDetailAction.DismissGroups -> local.update { it.copy(groupDialogOpen = false, draftGroupId = null) }
            is NoteDetailAction.DraftGroup -> local.update { it.copy(draftGroupId = action.id) }
            NoteDetailAction.ConfirmGroup -> if (!local.value.isGroupWorking) local.update { it.copy(note = it.note.copy(groupId = it.draftGroupId), groupDialogOpen = false) }
            NoteDetailAction.ManageGroups -> local.update { it.copy(groupManagementOpen = true, duplicateGroupName = false) }
            NoteDetailAction.CloseManagement -> if (!local.value.isGroupWorking) local.update { it.copy(groupManagementOpen = false, duplicateGroupName = false) }
            is NoteDetailAction.AddGroup -> addGroup(action.title)
            is NoteDetailAction.RenameGroup -> renameGroup(action.id, action.title)
            is NoteDetailAction.DeleteGroup -> deleteGroup(action.id)
            NoteDetailAction.Retry -> { refresh.update { it + 1 }; if (local.value.noteMissing || local.value.isLoading) load() else local.update { it.copy(hasError = false, duplicateGroupName = false) } }
        }
    }

    private fun load() {
        local.update { it.copy(isLoading = true, hasError = false, duplicateGroupName = false, noteMissing = false) }
        operation {
            val note = requestedId?.let { requireNotNull(noteRepository.getNoteById(it)) } ?: Note()
            original = note
            local.update { it.copy(note = note, isLoading = false) }
        }
    }
    private fun save() {
        if (!local.value.fieldsEnabled) return
        val draft = local.value
        if (draft.note.title.isBlank()) {
            local.update { it.copy(titleError = ValidationStatus(isSuccessful = false, messageId = Res.string.error_empty_field_label, args = arrayOf(Res.string.title_label))) }
            return
        }
        local.update { it.copy(isSaving = true, hasError = false, duplicateGroupName = false) }
        operation {
            // Resolve a persisted ID, never an option's position; deleted groups become no group.
            val groupId = draft.note.groupId?.takeIf { groupRepository.getGroupById(it) != null }
            val saved = mutations.save(draft.note.copy(groupId = groupId, imageNames = draft.note.imageNames.filterNot { it in draft.removedImageNames }), draft.selectedImages)
            original = saved
            local.update { it.copy(note = saved, selectedImages = null, removedImageNames = emptySet(), isSaving = false) }
            super.onAction(BaseAction.OnNavigateTo(null))
        }
    }
    private fun addGroup(title: String) {
        if (title.isBlank() || local.value.isGroupWorking) return
        local.update { it.copy(isGroupWorking = true, hasError = false, duplicateGroupName = false) }
        operation {
            val normalized = title.trim()
            val id = groupRepository.upsertGroup(NoteGroup.create(normalized, languageProvider.currentLanguage))
            local.update { it.copy(draftGroupId = id, isGroupWorking = false, groupAddedVersion = it.groupAddedVersion + 1) }
        }
    }
    private fun renameGroup(id: Long, title: String) {
        if (title.isBlank() || local.value.isGroupWorking) return
        local.update { it.copy(isGroupWorking = true, hasError = false, duplicateGroupName = false) }
        operation {
            val group = requireNotNull(groupRepository.getGroupById(id))
            if (!group.isDefault) groupRepository.upsertGroup(group.renamed(title, languageProvider.currentLanguage))
            local.update { it.copy(isGroupWorking = false, groupAddedVersion = it.groupAddedVersion + 1) }
        }
    }
    private fun deleteGroup(id: Long) {
        if (local.value.isGroupWorking) return
        local.update { it.copy(isGroupWorking = true, hasError = false, duplicateGroupName = false) }
        operation {
            groupRepository.deleteGroup(id)
            if (original.groupId == id) original = original.copy(groupId = null)
            local.update { it.copy(isGroupWorking = false,
                note = if (it.note.groupId == id) it.note.copy(groupId = null) else it.note,
                draftGroupId = it.draftGroupId.takeUnless { groupId -> groupId == id }) }
        }
    }
    private fun operation(block: suspend () -> Unit) {
        viewModelScope.launch {
            try { block() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: DuplicateNameException) { local.update { it.copy(duplicateGroupName = true, isGroupWorking = false) } }
            catch (_: Exception) { local.update { it.copy(hasError = true, noteMissing = it.isLoading && requestedId != null,
                isSaving = false, isLoading = false, isGroupWorking = false) } }
        }
    }
    override fun onResetState() = Unit
}
