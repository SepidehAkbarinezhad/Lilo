package com.sepideh.lilo.task.presentation.task_detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailEvent
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.category_icon
import lilo.composeapp.generated.resources.date_icon
import lilo.composeapp.generated.resources.priority_icon
import lilo.composeapp.generated.resources.time_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskDetailIcons(onEvent: (BaseEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { onEvent(TaskDetailEvent.OnCategoryIcon) }) {
            Image(
                painter = painterResource(Res.drawable.category_icon),
                contentDescription = null
            )
        }
        IconButton(onClick = { onEvent(TaskDetailEvent.OnCategoryIcon) }) {
            Image(
                painter = painterResource(Res.drawable.priority_icon),
                contentDescription = null
            )
        }
        IconButton(onClick = { onEvent(TaskDetailEvent.OnDateIcon) }) {
            Image(
                painter = painterResource(Res.drawable.date_icon),
                contentDescription = null
            )
        }
        IconButton(onClick = { onEvent(TaskDetailEvent.OnTimeIcon) }) {
            Image(
                painter = painterResource(Res.drawable.time_icon),
                contentDescription = null
            )
        }
    }
}