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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.task.presentation.task_list.TaskListAction
import com.sepideh.lilo.task.presentation.task_list.TaskListState
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

@Composable
fun CategoryList(
    state: TaskListState,
    clickable: Boolean,
    onAction: (BaseAction) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = state.categories) { category ->

            // Determine if the category is selected or if it's the first one when selectedCategory is null
            val isSelected =
                category.id == state.selectedCategory || (state.selectedCategory == null && category == state.categories.first())
            val titleColor =
                if (isSelected) LiloExtendedTheme.colors.selectedCategory else LiloExtendedTheme.colors.unSelectedCategory

            AppText(
                modifier = Modifier.widthIn(min = 100.dp).border(
                    width = 1.dp,
                    color = titleColor,
                    shape = RoundedCornerShape(8.dp),
                ).padding(4.dp)
                    .clickable(
                        indication = null, // Disable the ripple effect
                        interactionSource = remember { MutableInteractionSource() } // Prevent the ripple interaction
                    ) {
                        if (clickable) onAction(
                            TaskListAction.OnCategorySelected(
                                category.id
                            )
                        )
                    },
                text = category.title,
                textAlign = TextAlign.Center,
                color = titleColor,
                textType = TextType.SubTitle
            )
        }
    }
}