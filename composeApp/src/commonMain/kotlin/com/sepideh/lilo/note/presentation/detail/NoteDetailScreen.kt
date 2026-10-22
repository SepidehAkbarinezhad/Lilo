package com.sepideh.lilo.note.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.domain.images.IMAGE_SELECTION_LIMIT
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.group.GroupOption
import com.sepideh.lilo.core.presentation.components.picker.rememberImagePicker
import com.sepideh.lilo.core.presentation.images.attachmentPreviews
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.detail.components.*
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoteDetailScreenRoot(noteId: Long?, viewModel: NoteDetailViewModel, onNavigateTo: (AppRoutes) -> Unit, onBack: () -> Boolean) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(noteId) { viewModel.onAction(NoteDetailAction.Load(noteId)) }
    AppBackHandler(enabled = !state.groupDialogOpen && !state.groupManagementOpen) {
        viewModel.onAction(NoteDetailAction.Back)
    }
    val pickImages = rememberImagePicker(
        onSelected = { viewModel.onAction(NoteDetailAction.Images(it)) },
        onError = { viewModel.onAction(NoteDetailAction.ImageError) },
        maxSelection = (IMAGE_SELECTION_LIMIT - attachmentPreviews(
            state.note.imageNames, state.removedImageNames, state.selectedImages,
        ).size).coerceAtLeast(1),
    )
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack,
        bodyContainer = {
            NoteDetailScreen(state.copy(isEditing = noteId != null)) { action ->
                when (action) {
                    NoteDetailAction.PickImages -> pickImages()
                    else -> viewModel.onAction(action)
                }
            }
        },
        dialogContent = { NoteEditorDialogs(state, viewModel::onAction) })
}

@Composable
fun NoteDetailScreen(state: NoteDetailState, onAction: (BaseAction) -> Unit) {
    val accent = LiloExtendedTheme.colors.noteColor
    BaseFormScreen(title = if (state.isEditing) Res.string.edit_note_title else Res.string.add_note_title,
        accent = accent, saveEnabled = state.fieldsEnabled, textSaveAction = true,
        saveActionColor = LiloExtendedTheme.colors.noteAction, actionLabel = Res.string.note_save_action,
        onBack = { onAction(NoteDetailAction.Back); true }, onSave = { onAction(NoteDetailAction.Save) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(LiloSpacing.Screen),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Section)) {
            if (state.isLoading || state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            if (state.hasError) {
                AppText(text = Res.string.note_operation_error, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { onAction(NoteDetailAction.Retry) }) { Text(stringResource(Res.string.note_retry)) }
            }
            NoteEditorFields(state, onAction)
        }
    }
}

@AppPreviews
@Composable
private fun NoteEditorPreview() {
    LiloPreviewWrapper {
        NoteDetailScreen(NoteDetailState(isLoading = false, isEditing = true,
            note = Note(title = "A little inspiration", content = "Ideas worth keeping.", groupId = 1),
            groups = listOf(GroupOption(1, "Personal"))), {})
    }
}

@AppPreviews
@Composable
private fun NoteNewPreview() {
    LiloPreviewWrapper { NoteDetailScreen(NoteDetailState(isLoading = false), {}) }
}
