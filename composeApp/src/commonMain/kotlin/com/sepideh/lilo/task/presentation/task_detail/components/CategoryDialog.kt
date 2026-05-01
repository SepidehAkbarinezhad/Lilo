package com.sepideh.lilo.task.presentation.task_detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.category.GENERAL_CATEGORY
import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.category_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryDialog(state: TaskDetailState, onAction: (BaseAction) -> Unit) {
    var selected by remember { mutableStateOf(state.selectedCategory) }
    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
    var categoriesEmpty by remember { mutableStateOf(state.categories.isEmpty()) }


    AppDialog(dialogModel = DialogModel(content = {
        Column(modifier = Modifier.fillMaxWidth()) {
            DialogTitle()
            Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))
            Column(
                modifier = Modifier.heightIn(max = 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                state.categories.forEachIndexed { index, category ->

                    CategoryItem(
                        category = category,
                        selected = selected,
                        uncheckedColor = contentColor,
                        onCheckedChange = { selected = it },
                        onDeleteCategory = { id -> onAction(TaskDetailAction.OnDeleteCategory(id)) }
                    )

                    if (index != state.categories.lastIndex) {
                        Spacer(
                            modifier = Modifier.fillMaxWidth().height(1.dp)
                                .background(contentColor)
                        )
                    }
                }
            }
            if (!categoriesEmpty) {
                CategoryDialogButtons(
                    onDone = {
                        selected?.let {
                            onAction(TaskDetailAction.OnCategorySelected(it))
                        }
                    }, onAddNewCategory = { onAction(TaskDetailAction.OnAddNewCategory(it)) })
            } else {
                AddCategory(
                    addVisibility = true,
                    onAddNewCategory = { onAction(TaskDetailAction.OnAddNewCategory(it)) })


            }

        }

    }, onDismissRequest = { onAction(TaskDetailAction.OnDismissCategoryDialog) }))
}

@Composable
fun DialogTitle() {
    Box(modifier = Modifier.fillMaxWidth()) {
        AppText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(Res.string.category_label),
            textType = TextType.Title,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun CategoryItem(
    category: CategoryPresentation,
    selected: CategoryPresentation?,
    uncheckedColor: Color,
    onCheckedChange: (CategoryPresentation) -> Unit,
    onDeleteCategory: (Long) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selected == category, onCheckedChange = {
                    onCheckedChange(category)
                },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = uncheckedColor
                )
            )
            AppText(
                text = category.title,
                textType = TextType.SubTitle,
                color = if (selected == category) MaterialTheme.colorScheme.primary else uncheckedColor
            )
        }

        if (category.title != GENERAL_CATEGORY) {
            IconButton(onClick = { onDeleteCategory(category.id) }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "delete Icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

    }
}

@Composable
fun CategoryDialogButtons(
    onDone: () -> Unit,
    onAddNewCategory: (String) -> Unit
) {
    var addVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {

        if (!addVisibility) {
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = {
                        addVisibility = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = onDone
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        AddCategory(
            addVisibility = addVisibility,
            onAddNewCategory = {
                addVisibility = !addVisibility
                onAddNewCategory(it)
            }
        )

    }
}

@Composable
fun AddCategory(
    addVisibility: Boolean,
    onAddNewCategory: (String) -> Unit
) {
    var newCategory by remember { mutableStateOf("") }
    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(addVisibility) {
        if (addVisibility)
            focusRequester.requestFocus()
    }

    AnimatedVisibility(
        visible = addVisibility,
        enter = fadeIn() + slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        ),
        exit = fadeOut() + slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCategory,
                onValueChange = { newCategory = it },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = contentColor,
                    focusedBorderColor = contentColor,
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
            )
            IconButton(
                onClick = {
                    if (newCategory.isNotEmpty()) {
                        onAddNewCategory(newCategory)
                    }

                    newCategory = ""

                }
            ) {
                Icon(
                    imageVector = if (newCategory.isNotEmpty()) Icons.Default.Done else Icons.Default.Close,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun CategoryDialogPrev() {
    CategoryDialog(
        state = TaskDetailState(
            categories = listOf(
                CategoryPresentation(
                    title = "test",
                    isDeletable = true
                )
            )
        ),
        onAction = {}
    )
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun CategoryDialogEmptyPrev() {
    CategoryDialog(
        state = TaskDetailState(categories = emptyList()),
        onAction = {}
    )
}

