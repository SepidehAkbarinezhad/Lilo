package com.sepideh.lilo.task.data.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int =0,
    val title : String,
)
