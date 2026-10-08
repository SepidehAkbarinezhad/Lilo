package com.sepideh.lilo.category.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_groups")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long =0,
    val titleEn : String ,
    val titleFa : String
)