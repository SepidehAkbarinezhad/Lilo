package com.sepideh.lilo.note.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_groups")
data class NoteGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleEn: String,
    val titleFa: String,
    val isDefault: Boolean = false,
)
