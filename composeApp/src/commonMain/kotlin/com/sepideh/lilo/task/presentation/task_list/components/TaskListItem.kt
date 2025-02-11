package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.TextType
import com.sepideh.lilo.task.domain.Task

@Composable
fun TaskListItem(modifier: Modifier = Modifier, task: Task, onTaskClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onTaskClick), shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppText(text = task.title, textType = TextType.SubTitle)
            AppText(text = task.description, textType = TextType.Body)
        }
    }
}