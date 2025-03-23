package com.sepideh.lilo.task.presentation.model

import androidx.compose.ui.graphics.Color


data class Priority(
    val id: Int ,
    val title: String = "",
    val color: Color
) {
    companion object {
        val priorities = listOf(
            Priority(id = 0, title = "high", color = Color.Red),
            Priority(id = 1, title = "middle", color = Color.Green),
            Priority(id = 2, title = "low", color = Color.Yellow),
        )
        fun getByTitle(title: String):Priority= priorities.find { it.title == title }?: priorities[0]
    }
}
