package com.sepideh.lilo.task.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long =0,
    val title : String,
    val description : String
)
