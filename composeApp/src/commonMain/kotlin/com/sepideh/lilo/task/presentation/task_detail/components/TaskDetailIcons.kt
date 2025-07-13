package com.sepideh.lilo.task.presentation.task_detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.category_icon
import lilo.composeapp.generated.resources.date_icon
import lilo.composeapp.generated.resources.priority_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskDetailIcons(onAction: (BaseAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { onAction(TaskDetailAction.OnCategoryIcon) }) {
            Image(
                painter = painterResource(Res.drawable.category_icon),
                contentDescription = null
            )
        }
        IconButton(onClick = { onAction(TaskDetailAction.OnPriorityIcon) }) {
            Image(
                painter = painterResource(Res.drawable.priority_icon),
                contentDescription = null
            )
        }
        IconButton(onClick = { onAction(TaskDetailAction.OnDateReminderIcon) }) {
            Image(
                painter = painterResource(Res.drawable.date_icon),
                contentDescription = null
            )
        }
    }
}