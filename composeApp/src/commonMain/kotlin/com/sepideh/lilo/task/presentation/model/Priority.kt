package com.sepideh.lilo.task.presentation.model

import androidx.compose.ui.graphics.Color
import com.sepideh.lilo.task.domain.model.TaskPriority
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.priority_high_label
import lilo.composeapp.generated.resources.priority_low_label
import lilo.composeapp.generated.resources.priority_middle_label
import org.jetbrains.compose.resources.StringResource

data class Priority(
    val id: Int,
    val title: StringResource,

    val value: TaskPriority
) {
    val color: Color
        @androidx.compose.runtime.Composable get() = when (value) {
            TaskPriority.HIGH -> com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.priorityHigh
            TaskPriority.MEDIUM -> com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.priorityMedium
            TaskPriority.LOW -> com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.priorityLow
        }
    companion object {
        val priorities = listOf(
            Priority(id = 0, title = Res.string.priority_high_label, value = TaskPriority.HIGH),
            Priority(id = 1, title = Res.string.priority_middle_label, value = TaskPriority.MEDIUM),
            Priority(id = 2, title = Res.string.priority_low_label, value = TaskPriority.LOW),
        )
        fun getById(id: Int): Priority = priorities.find { it.id == id } ?: priorities.first { it.value == TaskPriority.MEDIUM }

        fun getByValue(value: TaskPriority): Priority = priorities.first { it.value == value }

        fun getByTitle(title: StringResource): Priority =
            priorities.find { it.title == title } ?: priorities[0]
    }
}