package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.model.Priority.Companion.priorities
import com.sepideh.lilo.task.presentation.task_list.TaskListEvent
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.delete_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskListItem(modifier: Modifier = Modifier, task: Task, onEvent: (BaseEvent) -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = {}), shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
    ) {
        with(task){
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = done, onCheckedChange = {onEvent(TaskListEvent.OnDoneChange(task=task.copy(done = !done)))})
                Column(
                    Modifier.weight(.8f).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(text = title, textType = TextType.SubTitle, textDecoration =  if(task.done) TextDecoration.LineThrough else TextDecoration.None )
                    AppText(text = description, textType = TextType.Body)
                    AppText(text = category.toString(), textType = TextType.Body)
                }
                IconButton(onClick = {
                    onEvent(TaskListEvent.OnDeleteTask(task))}) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "selete Icon",
                        tint = Color.White
                    )
                }
                Box(modifier = Modifier.weight(.03f).fillMaxHeight().background(color = priorities[priority].color))

            }
        }


    }
}