package com.sepideh.lilo.task.presentation.model

data class Category(
    val id: Int = 0,
    val title: String = ""
) {
    companion object {
        val categories = listOf(
            Category(id = 1, title = "all"),
            Category(id = 2, title = "work"),
            Category(id = 3, title = "hobby"),
            Category(id = 4, title = "music"),
        )
    }
}
