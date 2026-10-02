package com.sepideh.lilo.core.presentation.components.selection

import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.sepideh.lilo.core.presentation.SheetHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import androidx.compose.ui.unit.dp
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Only values and callbacks: usable by any feature and any repository. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelectionSheet(
    groups: List<GroupOption>, selectedId: Long?, accent: Color,
    isAdding: Boolean, addedVersion: Int,
    onSelect: (Long?) -> Unit, onCreate: (String) -> Unit,
    onManage: () -> Unit, onConfirm: () -> Unit, onDismiss: () -> Unit,
) {
    var adding by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(addedVersion) { if (addedVersion > 0) { adding = false; name = "" } }
    ModalBottomSheet(onDismissRequest = { if (!isAdding) onDismiss() }, containerColor = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = 20.dp)) {
            SheetHeader(Res.string.choose_group_title, Res.string.confirm_action, accent, !adding && !isAdding, onConfirm)
            LazyColumn(Modifier.selectableGroup().fillMaxWidth().heightIn(min = 240.dp, max = 360.dp)) {
                item {
                    GroupSelectionRow(stringResource(Res.string.no_group_label), selectedId == null, accent) { onSelect(null) }
                }
                items(groups, key = { it.id }) { group ->
                    GroupSelectionRow(group.label, selectedId == group.id, accent) { onSelect(group.id) }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .5f))
            Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                if (adding) {
                    val focus = remember { FocusRequester() }
                    LaunchedEffect(Unit) { focus.requestFocus() }
                    OutlinedTextField(value = name, onValueChange = { name = it }, enabled = !isAdding,
                        singleLine = true, modifier = Modifier.weight(1f).focusRequester(focus), shape = RoundedCornerShape(10.dp), textStyle = MaterialTheme.typography.bodyLarge, placeholder = { AppText(text = Res.string.new_group_hint) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent, cursorColor = accent))
                    IconButton(onClick = { onCreate(name.trim()) }, enabled = name.isNotBlank() && !isAdding) {
                        Icon(Icons.Outlined.Check, stringResource(Res.string.confirm_action), tint = accent, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { adding = false; name = "" }, enabled = !isAdding) {
                        Icon(Icons.Outlined.Close, stringResource(Res.string.cancel_button), modifier = Modifier.size(20.dp))
                    }
                } else {
                    IconButton(onClick = { adding = true }) {
                        Icon(Icons.Outlined.Add, stringResource(Res.string.add_group_action), modifier = Modifier.size(20.dp), tint = accent)
                    }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = onManage, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                        AppText(text = Res.string.manage_groups_action, textType = TextType.Action)
                    }
                }
            }
        }
    }
}

@com.sepideh.lilo.core.presentation.components.AppPreviews
@Composable
private fun GroupSelectionSheetPreview() {
    com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper {
        GroupSelectionSheet(
            groups = listOf(GroupOption(1, "Personal"), GroupOption(2, "Work")),
            selectedId = 1, accent = Color(0xFFFFC107), isAdding = false, addedVersion = 0,
            onSelect = {}, onCreate = {}, onManage = {}, onConfirm = {}, onDismiss = {},
        )
    }
}
