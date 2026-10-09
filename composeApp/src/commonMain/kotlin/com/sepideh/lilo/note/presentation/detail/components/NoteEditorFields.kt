package com.sepideh.lilo.note.presentation.detail.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.format.localizedDigits
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.form.FormSelectionRow
import com.sepideh.lilo.core.presentation.components.picker.rememberImagePicker
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.note.presentation.detail.*
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@Composable fun NoteEditorFields(state:NoteDetailState,action:(BaseAction)->Unit) {
 val accent=LiloExtendedTheme.colors.noteColor
 val pick=rememberImagePicker(onSelected={action(NoteDetailAction.Images(it))},onError={action(NoteDetailAction.ImageError)})
 val count=state.selectedImages?.size?:state.note.imageNames.size
 AppOutlineTextField(accentColor=accent,leadingIcon={Icon(LiloIcons.Edit,null,Modifier.size(LiloSize.Icon))},
  textFieldRequired=TextFieldRequired(label=stringResource(Res.string.title_label),isRequired=true,value=state.note.title,onValueChange={action(NoteDetailAction.Title(it))},enabled=state.fieldsEnabled,validationStatus=state.titleError))
 AppOutlineTextField(accentColor=accent,singleLine=false,maxLines=Int.MAX_VALUE,topAlignedIcon=true,textFieldModifier=Modifier.heightIn(min=180.dp),
  leadingIcon={Icon(LiloIcons.Description,null,Modifier.size(LiloSize.Icon))},
  textFieldRequired=TextFieldRequired(label=stringResource(Res.string.note_content_label),value=state.note.content,onValueChange={action(NoteDetailAction.Content(it))},enabled=state.fieldsEnabled))
 FormSelectionRow(LiloIcons.Groups,stringResource(Res.string.group_field_label),state.selectedGroup?.label?:stringResource(Res.string.no_group_label),{action(NoteDetailAction.OpenGroups)},enabled=state.fieldsEnabled,accent=accent)
 FormSelectionRow(LiloIcons.Images,stringResource(Res.string.note_images_label),stringResource(Res.string.note_images_count,count).localizedDigits(LocalLayoutDirection.current == LayoutDirection.Rtl),pick,enabled=state.fieldsEnabled,accent=accent,
  onClear=if(count>0) ({action(NoteDetailAction.ClearImages)}) else null,clearContentDescription=stringResource(Res.string.note_clear_images))
}
