package com.sepideh.lilo.task.presentation.model

data class Category(
    val id: Int = 0,
    val title: String = ""
) {
    companion object {
        val categories = listOf(
            Category(title = "all"),
            Category(title = "work"),
            Category(title = "hobby"),
            Category(title = "music"),
        )
    }
}
