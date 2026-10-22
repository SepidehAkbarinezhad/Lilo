package com.sepideh.lilo.task.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    foreignKeys = [ForeignKey(
        entity = TaskGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"],
        onDelete = ForeignKey.SET_NULL,
    )],
    indices = [Index("groupId")],
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val done: Boolean = false,
    val groupId: Long? = null,
    val priority: Int = 0,
    val reminderAt: Long? = null,
    val repeatRule: String = "NONE",
    val reminderTimeZoneId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long? = null,
    @androidx.room.ColumnInfo(defaultValue = "'[]'") val imageNames: String = "[]"
)
