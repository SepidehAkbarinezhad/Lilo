package com.sepideh.lilo.note.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = NoteGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"],
        onDelete = ForeignKey.SET_NULL,
    )],
    indices = [Index("groupId"), Index("updatedAt")],
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val groupId: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "'[]'") val imageNames: String = "[]",
)
