package com.sepideh.lilo.note.presentation.detail.components
import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.EditCancellationDialog
import com.sepideh.lilo.core.presentation.components.group.*
import com.sepideh.lilo.note.presentation.detail.*
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@Composable fun NoteEditorDialogs(state:NoteDetailState,action:(BaseAction)->Unit) {
 val accent=LiloExtendedTheme.colors.notes.accent
 if(state.groupDialogOpen) GroupSelectionSheet(groups=state.groups,selectedId=state.draftGroupId,accent=accent,addedVersion=state.groupAddedVersion,
  onSelect={action(NoteDetailAction.DraftGroup(it))},onManage={action(NoteDetailAction.ManageGroups)},onConfirm={action(NoteDetailAction.ConfirmGroup)},onDismiss={action(NoteDetailAction.CloseManagement); action(NoteDetailAction.DismissGroups)},
  busy=state.isGroupWorking,
  managementContent=if(state.groupManagementOpen) ({ GroupManagementEditor(groups=state.groups,accent=accent,closeActionColor=LiloExtendedTheme.colors.notes.action,selectedId=state.draftGroupId,isAdding=state.isGroupWorking,addedVersion=state.groupAddedVersion,
  onCreate={action(NoteDetailAction.AddGroup(it))},onRename={id,title->action(NoteDetailAction.RenameGroup(id,title))},onDelete={action(NoteDetailAction.DeleteGroup(it))},onDismiss={action(NoteDetailAction.CloseManagement)},
  errorMessage=if(state.duplicateGroupName) stringResource(Res.string.group_name_exists) else if(state.hasError) stringResource(Res.string.note_operation_error) else null,deleteMessage=stringResource(Res.string.note_delete_group_message)) }) else null)
 if(state.confirmDiscard) EditCancellationDialog(accent,
  onKeepEditing={action(NoteDetailAction.KeepEditing)},onDiscard={action(NoteDetailAction.Discard)})
}
