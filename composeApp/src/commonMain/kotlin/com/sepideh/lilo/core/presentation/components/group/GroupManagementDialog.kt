package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagementDialog(groups: List<GroupOption>, onDelete: (Long) -> Unit,
    onDismiss: () -> Unit, errorMessage: String? = null, accent: Color,
    selectedId: Long? = null, isAdding: Boolean = false, addedVersion: Int = 0, onCreate: ((String) -> Unit)? = null, onRename: ((Long, String) -> Unit)? = null) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var editing by remember { mutableStateOf(false) }
    var renameId by remember { mutableStateOf<Long?>(null) }
    var renameTitle by remember { mutableStateOf("") }
    LaunchedEffect(addedVersion) { if (addedVersion > 0) { renameId = null; renameTitle = "" } }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    ModalBottomSheet(onDismissRequest = { if (!isAdding) onDismiss() },
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = LiloSpacing.Screen).padding(bottom = LiloSpacing.Section), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
            Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                AppText(text = Res.string.manage_groups_action, textType = TextType.SectionTitle,
                    modifier = Modifier.weight(1f).padding(end = 12.dp))
                TextButton(enabled = !isAdding, onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }) {
                    AppText(text = Res.string.close_group_management_action, textType = TextType.Action, color = accent)
                }
            }
            GroupManagementContent(groups, errorMessage, { deleteId = it }, accent, selectedId,
                !isAdding && renameId == null && !editing, Modifier.weight(1f, fill = false),
                onEdit = if (onRename != null) ({ group -> renameId = group.id; renameTitle = group.label }) else null,
                addedVersion = addedVersion)
            if (renameId != null && onRename != null) {
                val focus = remember { androidx.compose.ui.focus.FocusRequester() }
                LaunchedEffect(renameId) { focus.requestFocus() }
                fun submitRename() { if (!isAdding && renameTitle.isNotBlank()) onRename(renameId!!, renameTitle.trim()) }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = renameTitle, onValueChange = { renameTitle = it }, singleLine = true,
                        enabled = !isAdding, modifier = Modifier.weight(1f).focusRequester(focus),
                        label = { AppText(text = Res.string.edit_group_action) }, shape = MaterialTheme.shapes.medium,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { submitRename() }),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent, cursorColor = accent))
                    IconButton(onClick = { submitRename() }, enabled = !isAdding && renameTitle.isNotBlank()) {
                        Icon(LiloIcons.Check, stringResource(Res.string.confirm_action))
                    }
                    IconButton(onClick = { renameId = null; renameTitle = "" }, enabled = !isAdding) {
                        Icon(LiloIcons.Close, stringResource(Res.string.cancel_button))
                    }
                }
            }
            if (onCreate != null && renameId == null) InlineGroupEditor(accent, isAdding, addedVersion, onCreate, onEditingChanged = { editing = it })
        }
    }
    deleteId?.let { id -> DeleteConfirmationDialog(accent = accent, title = stringResource(Res.string.delete_group_title), message = stringResource(Res.string.delete_group_message), confirmFirst = true,
        onConfirm = { onDelete(id); deleteId = null }, onDismiss = { deleteId = null }) }
}

@Composable
fun GroupManagementContent(groups: List<GroupOption>, errorMessage: String? = null, onDelete: (Long) -> Unit,
    accent: Color = LiloExtendedTheme.colors.taskColor, selectedId: Long? = null, enabled: Boolean = true, modifier: Modifier = Modifier, onEdit: ((GroupOption) -> Unit)? = null, addedVersion: Int = 0) {
    val listState = rememberLazyListState()
    LaunchedEffect(addedVersion, groups.map { it.id }) {
        if (addedVersion > 0) {
            val index = groups.indexOfFirst { it.id == selectedId }
            if (index >= 0) listState.animateScrollToItem(index)
        }
    }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
        if (errorMessage != null) AppText(text = errorMessage, color = MaterialTheme.colorScheme.error)
        if (groups.isEmpty()) AppText(text = Res.string.no_group_label, color = LiloExtendedTheme.colors.textSupporting, modifier = Modifier.padding(vertical = LiloSpacing.Section))
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 360.dp), state = listState, verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
            items(groups, key = { it.id }) { group ->
                Surface(shape = MaterialTheme.shapes.medium,
                    color = if (group.id == selectedId) accent.copy(alpha = .08f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .55f))) {
                    Row(Modifier.fillMaxWidth().heightIn(min = LiloSize.SelectionRow).padding(start = 16.dp, end = LiloSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                        AppText(text = group.label, textType = TextType.BodyLarge, modifier = Modifier.weight(1f).padding(vertical = LiloSpacing.Small), maxLines = 2)
                        if (group.id == selectedId) Icon(LiloIcons.Check, null, Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.title)
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 40.dp) {
                        if (onEdit != null && group.isEditable) IconButton(onClick = { onEdit(group) }, enabled = enabled, modifier = Modifier.width(40.dp).height(48.dp)) {
                            Icon(LiloIcons.Edit, stringResource(Res.string.edit_group_action), Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.textSecondary)
                        }
                        if (group.isDeletable) IconButton(onClick = { onDelete(group.id) }, enabled = enabled, modifier = Modifier.width(40.dp).height(48.dp)) {
                            Icon(LiloIcons.Delete, stringResource(Res.string.delete_action), Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.textSecondary)
                        }
                        }
                    }
                }
            }
        }
    }
}
@AppPreviews
@Composable
private fun GroupManagementPreview() {
    LiloPreviewWrapper { GroupManagementContent(listOf(GroupOption(1, "Personal"), GroupOption(2, "Work")), onDelete = {}, selectedId = 1) }
}
