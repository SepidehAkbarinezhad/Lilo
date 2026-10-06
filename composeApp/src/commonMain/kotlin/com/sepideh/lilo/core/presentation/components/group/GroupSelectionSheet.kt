package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelectionSheet(groups: List<GroupOption>, selectedId: Long?, accent: Color,
    onSelect: (Long?) -> Unit,
    onManage: () -> Unit, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
        GroupSelectionContent(groups, selectedId, accent, onSelect, onManage, onConfirm)
    }
}

@Composable
fun GroupSelectionContent(groups: List<GroupOption>, selectedId: Long?, accent: Color,
    onSelect: (Long?) -> Unit,
    onManage: () -> Unit, onConfirm: () -> Unit) {
    Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = LiloSpacing.Screen).padding(bottom = LiloSpacing.Section), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
        SheetHeader(Res.string.choose_group_title, Res.string.confirm_action, accent, true, onConfirm)
        LazyColumn(Modifier.weight(1f, fill = false).selectableGroup().fillMaxWidth().heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
            item { GroupSelectionRow(stringResource(Res.string.no_group_label), selectedId == null, accent, onClick = { onSelect(null) }) }
            items(groups, key = { it.id }) { group -> GroupSelectionRow(group.label, selectedId == group.id, accent, onClick = { onSelect(group.id) }) }
        }
        OutlinedButton(onClick = onManage, modifier = Modifier.fillMaxWidth().heightIn(min = LiloSize.TouchTarget),
            shape = MaterialTheme.shapes.medium, colors = ButtonDefaults.outlinedButtonColors(contentColor = LiloExtendedTheme.colors.textPrimary)) {
            Icon(LiloIcons.Groups, null, Modifier.size(LiloSize.SmallIcon))
            Spacer(Modifier.width(LiloSpacing.Small))
            AppText(text = Res.string.manage_groups_action, textType = TextType.Action)
        }
    }
}

@AppPreviews
@Composable
private fun GroupSelectionPreview() {
    LiloPreviewWrapper { GroupSelectionContent(listOf(GroupOption(1, "Personal"), GroupOption(2, "Work")), 1,
        LiloExtendedTheme.colors.taskColor, {}, {}, {}) }
}
