package com.sepideh.lilo.task.presentation.task_detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailEvent
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.category_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoryDialog(state: TaskDetailState, onEvent: (BaseEvent) -> Unit) {

    var selected by remember { mutableStateOf(state.selectedCategory) }
    AppDialog(dialogModel = DialogModel(content = {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(bottom = 56.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AppText(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(Res.string.category_label),
                        textType = TextType.Title,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                state.categories.forEachIndexed { index, category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = selected == category, onCheckedChange = {
                            selected = category
                        })
                        AppText(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = category.title,
                            textType = TextType.SubTitle,
                            color = if (selected == category) MaterialTheme.colorScheme.primary else Color.Black
                        )
                    }

                    if (index != state.categories.lastIndex) {
                        Spacer(
                            modifier = Modifier.fillMaxWidth().height(1.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }
            AddCategoryContainer(onDone = {
                onEvent(
                    TaskDetailEvent.OnCategorySelected(
                        selected?.title ?: ""
                    )
                )
            })

        }

    }, onDismissRequest = { onEvent(TaskDetailEvent.OnDismissCategoryDialog) }))
}

@Composable
fun BoxScope.AddCategoryContainer(onDone: () -> Unit) {
    var doneVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .height(56.dp)
            .padding(8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        if (!doneVisibility) {
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = { doneVisibility = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = onDone
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = doneVisibility,
            enter = fadeIn() + slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ),
            exit = fadeOut() + slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { doneVisibility = !doneVisibility }
                ) {
                    Icon(
                        imageVector = if (!doneVisibility) Icons.Default.Add else Icons.Default.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

