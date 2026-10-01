package com.sepideh.lilo.core.presentation.components.selection

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

data class GroupOption(val id: Long, val label: String)

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.choose_group_title), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onConfirm, enabled = !adding && !isAdding, colors = ButtonDefaults.textButtonColors(contentColor = accent)) {
                    Text(stringResource(Res.string.confirm_action))
                }
            }
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                item {
                    GroupRow(stringResource(Res.string.no_group_label), selectedId == null, accent) { onSelect(null) }
                }
                items(groups, key = { it.id }) { group ->
                    GroupRow(group.label, selectedId == group.id, accent) { onSelect(group.id) }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .5f))
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (adding) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, enabled = !isAdding,
                        singleLine = true, modifier = Modifier.weight(1f), placeholder = { Text(stringResource(Res.string.new_group_hint)) },
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
                    TextButton(onClick = onManage, colors = ButtonDefaults.textButtonColors(contentColor = accent)) {
                        Text(stringResource(Res.string.manage_groups_action))
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(label: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        RadioButton(selected, onClick = null, modifier = Modifier.padding(12.dp), colors = RadioButtonDefaults.colors(selectedColor = accent))
    }
}
