package com.sepideh.lilo.note.presentation.detail
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.note.presentation.detail.components.*
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@Composable fun NoteDetailScreenRoot(noteId:Long?,viewModel:NoteDetailViewModel,onNavigateTo:(AppRoutes)->Unit,onBack:()->Boolean) {
 val state by viewModel.state.collectAsStateWithLifecycle()
 LaunchedEffect(noteId) { viewModel.onAction(NoteDetailAction.Load(noteId)) }
 com.sepideh.lilo.core.presentation.components.AppBackHandler(enabled=!state.groupDialogOpen&&!state.groupManagementOpen) { viewModel.onAction(NoteDetailAction.Back) }
 BaseRoot(viewModel=viewModel,navigateTo=onNavigateTo,onBack=onBack,
  bodyContainer={NoteDetailScreen(state,viewModel::onAction,isEditing=noteId!=null)},dialogContent={NoteEditorDialogs(state,viewModel::onAction)})
}
@Composable fun NoteDetailScreen(state:NoteDetailState,action:(BaseAction)->Unit,isEditing:Boolean) {
 val accent=LiloExtendedTheme.colors.noteColor
 BaseFormScreen(title=if(isEditing) Res.string.edit_note_title else Res.string.add_note_title,
  accent=accent,saveEnabled=state.fieldsEnabled,textSaveAction=true,saveActionColor=LiloExtendedTheme.colors.noteAction,actionLabel=Res.string.note_save_action,
  onBack={action(NoteDetailAction.Back);true},onSave={action(NoteDetailAction.Save)}) { padding ->
  Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(LiloSpacing.Screen),verticalArrangement=Arrangement.spacedBy(LiloSpacing.Section)) {
   if(state.isLoading||state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth(),color=accent)
   if(state.hasError) {
    AppText(text=Res.string.note_operation_error,color=MaterialTheme.colorScheme.error)
    TextButton(onClick={action(NoteDetailAction.Retry)}) { Text(stringResource(Res.string.note_retry)) }
   }
   NoteEditorFields(state,action)
  }
 }
}
@AppPreviews @Composable private fun NoteEditorPreview() { LiloPreviewWrapper { NoteDetailScreen(NoteDetailState(note=com.sepideh.lilo.note.domain.model.Note(title="A little inspiration",content="Ideas worth keeping."),isLoading=false),{},isEditing=false) } }
