package com.sepideh.lilo.task.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_groups")
data class TaskGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long =0,
    val titleEn : String ,
    val titleFa : String,
    val isDefault: Boolean = false
)