package com.sepideh.lilo.task.presentation.model

import androidx.compose.ui.graphics.Color
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.priority_high_label
import lilo.composeapp.generated.resources.priority_low_label
import lilo.composeapp.generated.resources.priority_middle_label
import org.jetbrains.compose.resources.StringResource


data class Priority(
    val id: Int,
    val title: StringResource,
    val color: Color
) {
    companion object {
        val priorities = listOf(
            Priority(id = 0, title = Res.string.priority_high_label, color = Color.Red),
            Priority(id = 1, title = Res.string.priority_middle_label, color = Color.Green),
            Priority(id = 2, title = Res.string.priority_low_label, color = Color.Yellow),
        )

        fun getByTitle(title: StringResource): Priority =
            priorities.find { it.title == title } ?: priorities[0]
    }
}
