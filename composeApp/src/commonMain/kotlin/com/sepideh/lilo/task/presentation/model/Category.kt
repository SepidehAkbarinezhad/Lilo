package com.sepideh.lilo.task.presentation.model

data class Category(
    val id: Long = 0 ,
    val title: String = ""
){
    companion object{
        val categories= listOf(Category(id = 1, title = "job"),Category(id = 2, title = "study"),Category(id = 3, title = "hobby"))
    }
}
