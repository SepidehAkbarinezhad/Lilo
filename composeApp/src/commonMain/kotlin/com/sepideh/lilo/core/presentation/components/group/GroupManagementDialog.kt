package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.SheetHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Uses the same sheet, rows and inline creation pattern as group selection. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagementDialog(groups: List<GroupOption>, deleteMessage: String, onDelete: (Long) -> Unit,
    onDismiss: () -> Unit, errorMessage: String? = null, accent: Color,
    selectedId: Long? = null, isAdding: Boolean = false, addedVersion: Int = 0, onCreate: ((String) -> Unit)? = null) {
    var editing by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    ModalBottomSheet(onDismissRequest = { if (!isAdding) onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = LiloSpacing.Screen).padding(bottom = LiloSpacing.Section), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
            SheetHeader(Res.string.manage_groups_action, Res.string.confirm_action, accent, !isAdding && !editing, onDismiss)
            GroupManagementContent(groups, errorMessage, { deleteId = it }, accent, selectedId, !isAdding, Modifier.weight(1f, fill = false))
            if (onCreate != null) InlineGroupEditor(accent, isAdding, addedVersion, onCreate, onEditingChanged = { editing = it })
        }
    }
    deleteId?.let { id -> DeleteConfirmationDialog(accent = accent, message = deleteMessage,
        onConfirm = { onDelete(id); deleteId = null }, onDismiss = { deleteId = null }) }
}

@Composable
fun GroupManagementContent(groups: List<GroupOption>, errorMessage: String? = null, onDelete: (Long) -> Unit,
    accent: Color = LiloExtendedTheme.colors.taskColor, selectedId: Long? = null, enabled: Boolean = true, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
        if (errorMessage != null) AppText(text = errorMessage, color = MaterialTheme.colorScheme.error)
        if (groups.isEmpty()) AppText(text = Res.string.no_group_label, color = LiloExtendedTheme.colors.textSupporting, modifier = Modifier.padding(vertical = LiloSpacing.Section))
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
            items(groups, key = { it.id }) { group ->
                Surface(shape = MaterialTheme.shapes.medium,
                    color = if (group.id == selectedId) accent.copy(alpha = .08f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .55f))) {
                    Row(Modifier.fillMaxWidth().heightIn(min = LiloSize.SelectionRow).padding(start = 16.dp, end = LiloSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                        AppText(text = group.label, textType = TextType.BodyLarge, modifier = Modifier.weight(1f).padding(vertical = LiloSpacing.Small), maxLines = 2)
                        if (group.id == selectedId) Icon(LiloIcons.Check, null, Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.title)
                        if (group.isDeletable) IconButton(onClick = { onDelete(group.id) }, enabled = enabled) {
                            Icon(LiloIcons.Delete, stringResource(Res.string.delete_action), Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.textSecondary)
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
