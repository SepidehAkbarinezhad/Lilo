package com.sepideh.lilo.note.presentation.detail.components
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.FeatureConfirmationContent
import com.sepideh.lilo.core.presentation.components.group.*
import com.sepideh.lilo.note.presentation.detail.*
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@Composable fun NoteEditorDialogs(state:NoteDetailState,action:(BaseAction)->Unit) {
 val accent=LiloExtendedTheme.colors.noteColor
 if(state.groupDialogOpen) GroupSelectionSheet(groups=state.groups,selectedId=state.draftGroupId,accent=accent,addedVersion=state.groupAddedVersion,
  onSelect={action(NoteDetailAction.DraftGroup(it))},onManage={action(NoteDetailAction.ManageGroups)},onConfirm={action(NoteDetailAction.ConfirmGroup)},onDismiss={action(NoteDetailAction.DismissGroups)})
 if(state.groupManagementOpen) GroupManagementDialog(groups=state.groups,accent=accent,selectedId=state.draftGroupId,isAdding=state.isGroupWorking,addedVersion=state.groupAddedVersion,
  onCreate={action(NoteDetailAction.AddGroup(it))},onRename={id,title->action(NoteDetailAction.RenameGroup(id,title))},onDelete={action(NoteDetailAction.DeleteGroup(it))},onDismiss={action(NoteDetailAction.CloseManagement)},
  errorMessage=if(state.hasError) stringResource(Res.string.note_operation_error) else null,deleteMessage=stringResource(Res.string.note_delete_group_message))
 if(state.confirmDiscard) Dialog(onDismissRequest={action(NoteDetailAction.KeepEditing)}) {
  FeatureConfirmationContent(accent,stringResource(Res.string.note_discard_title),stringResource(Res.string.note_discard_message),
   onConfirm={action(NoteDetailAction.Discard)},onDismiss={action(NoteDetailAction.KeepEditing)},confirmLabel=stringResource(Res.string.note_discard_action),cancelLabel=stringResource(Res.string.note_keep_editing))
 }
}
