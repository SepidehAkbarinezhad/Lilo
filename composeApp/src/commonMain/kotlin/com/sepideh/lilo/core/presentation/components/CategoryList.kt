package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.task.presentation.list.TaskListAction
import com.sepideh.lilo.task.presentation.list.TaskListState
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

@Composable
fun CategoryList(
    state: TaskListState,
    clickable: Boolean,
    onAction: (BaseAction) -> Unit
) {
    val selectedColor = LiloExtendedTheme.colors.taskColor
    val unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val unselectedBorderColor = MaterialTheme.colorScheme.outlineVariant

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = state.categories,
            key = { it.id }
        ) { category ->

            val isSelected =
                category.id == state.selectedCategory ||
                        (
                                state.selectedCategory == null &&
                                        category == state.categories.first()
                                )

            val textColor = if (isSelected) {
                selectedColor
            } else {
                unselectedTextColor
            }

            val borderColor = if (isSelected) {
                selectedColor
            } else {
                unselectedBorderColor
            }

            AppText(
                modifier = Modifier
                    .widthIn(min = 100.dp)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        enabled = clickable,
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        }
                    ) {
                        onAction(
                            TaskListAction.OnCategorySelected(category.id)
                        )
                    }
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                text = category.title,
                textAlign = TextAlign.Center,
                color = textColor,
                textType = TextType.SubTitle
            )
        }
    }
}