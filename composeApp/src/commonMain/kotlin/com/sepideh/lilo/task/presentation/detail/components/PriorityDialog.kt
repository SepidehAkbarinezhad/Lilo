package com.sepideh.lilo.task.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.detail.*
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*

/** Legacy entry point keeps draft selection until confirmation, using the same priority UI. */
@Composable
fun PriorityDialog(state: TaskDetailState, onAction: (BaseAction) -> Unit) {
    var selected by remember { mutableStateOf(state.selectedPriority) }
    val accent = LiloExtendedTheme.colors.taskColor
    Dialog(onDismissRequest = { onAction(TaskDetailAction.OnDismissPriorityDialog) }) {
        Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(LiloSpacing.Section), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
                TaskPrioritySelector(selected.id, accent, onSelect = { selected = it })
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onAction(TaskDetailAction.OnDismissPriorityDialog) }) { AppText(text = Res.string.cancel_button, textType = TextType.Action, color = LiloExtendedTheme.colors.textSecondary) }
                    TextButton(onClick = { onAction(TaskDetailAction.OnPrioritySelected(selected.title)) }) { AppText(text = Res.string.confirm_action, textType = TextType.Action, color = LiloExtendedTheme.colors.taskAction) }
                }
            }
        }
    }
}
