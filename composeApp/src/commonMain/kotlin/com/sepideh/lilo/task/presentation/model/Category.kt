package com.sepideh.lilo.task.presentation.model

data class Category(
    val id: Long = 0,
    val title: String = "",
    val secondTitle: String = ""
) {
    companion object {
        val categories =
            listOf(
                Category(title = "all", secondTitle = "همه"),
                Category(title = "general", secondTitle = "عمومی"),
                Category(title = "work", secondTitle = "کار"),
                Category(title = "hobby", secondTitle = "سرگرمی"),
                Category(title = "music", secondTitle = "موسیقی")
            )
    }
}
