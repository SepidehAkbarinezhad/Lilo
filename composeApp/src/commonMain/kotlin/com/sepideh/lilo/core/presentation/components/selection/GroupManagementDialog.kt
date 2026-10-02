package com.sepideh.lilo.core.presentation.components.selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.selection.GroupOption
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupManagementDialog(
    groups: List<GroupOption>,
    deleteMessage: String,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null,
) {
    var deleteId by remember { mutableStateOf<Long?>(null) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { AppText(text = Res.string.manage_groups_action, textType = TextType.SectionTitle) },
        text = {
            Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                if (errorMessage != null) Text(errorMessage, color = MaterialTheme.colorScheme.error)
                groups.forEach { group ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(group.label, Modifier.weight(1f))
                        if (group.isDeletable) IconButton(onClick = { deleteId = group.id }) { Icon(Icons.Outlined.Delete, stringResource(Res.string.delete_action), Modifier.size(20.dp)) }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { AppText(text = Res.string.confirm_action, textType = TextType.Action) } })
    deleteId?.let { id ->
        AlertDialog(onDismissRequest = { deleteId = null }, text = { Text(deleteMessage) },
            confirmButton = { TextButton(onClick = { onDelete(id); deleteId = null }) { AppText(text = Res.string.delete_action, textType = TextType.Action) } },
            dismissButton = { TextButton(onClick = { deleteId = null }) { AppText(text = Res.string.cancel_button, textType = TextType.Action) } })
    }
}

@AppPreviews
@Composable
private fun GroupManagementPreview() {
    LiloPreviewWrapper {
        GroupManagementDialog(listOf(GroupOption(1, "Personal"), GroupOption(2, "Work")),
            "Remove this group? Items will be kept.", {}, {})
    }
}
