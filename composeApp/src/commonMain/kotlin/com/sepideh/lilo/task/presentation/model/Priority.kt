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
    val color: Color,
    val value: TaskPriority
) {
    companion object {
        val priorities = listOf(
            Priority(id = 0, title = Res.string.priority_high_label, color = Color(0xFFEA4545), value = TaskPriority.HIGH),
            Priority(id = 1, title = Res.string.priority_middle_label, color = Color(0xFFFFC107), value = TaskPriority.MEDIUM),
            Priority(id = 2, title = Res.string.priority_low_label, color = Color(0xFF2BB86A), value = TaskPriority.LOW),
        )
        fun getById(id: Int): Priority = priorities.find { it.id == id } ?: priorities.first { it.value == TaskPriority.MEDIUM }

        fun getByValue(value: TaskPriority): Priority = priorities.first { it.value == value }

        fun getByTitle(title: StringResource): Priority =
            priorities.find { it.title == title } ?: priorities[0]
    }
}