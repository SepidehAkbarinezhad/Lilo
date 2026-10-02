package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.ui.tooling.preview.Preview
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
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupManagementDialog(
    groups: List<GroupOption>,
    deleteMessage: String,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null,
    accent: androidx.compose.ui.graphics.Color,
) {
    var deleteId by remember { mutableStateOf<Long?>(null) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { AppText(text = Res.string.manage_groups_action, textType = TextType.SectionTitle) },
        text = {
            GroupManagementContent(groups, errorMessage, onDelete = { deleteId = it })
        }, confirmButton = { TextButton(onClick = onDismiss) { AppText(text = Res.string.confirm_action, textType = TextType.Action) } })
    deleteId?.let { id ->
        DeleteConfirmationDialog(
            accent = accent, message = deleteMessage,
            onConfirm = { onDelete(id); deleteId = null },
            onDismiss = { deleteId = null },
        )
    }
}

@Composable
fun GroupManagementContent(groups: List<GroupOption>, errorMessage: String? = null, onDelete: (Long) -> Unit) {
    Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
        if (errorMessage != null) Text(errorMessage, color = MaterialTheme.colorScheme.error)
        groups.forEach { group ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(group.label, Modifier.weight(1f))
                if (group.isDeletable) IconButton(onClick = { onDelete(group.id) }) { Icon(Icons.Outlined.Delete, stringResource(Res.string.delete_action), Modifier.size(20.dp)) }
            }
        }
    }
}

@Preview(name = "Manage groups", showBackground = true, widthDp = 360)
@Composable
private fun GroupManagementPreview() {
    LiloPreviewWrapper {
        Surface {
            GroupManagementContent(listOf(GroupOption(1, "Personal"), GroupOption(2, "Work")), onDelete = {})
        }
    }
}
