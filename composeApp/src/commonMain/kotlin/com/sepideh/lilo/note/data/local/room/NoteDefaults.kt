package com.sepideh.lilo.note.data.local.room

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection

internal val defaultNoteGroups = listOf(
    NoteGroupEntity(titleEn = "Personal", titleFa = "شخصی", isDefault = true),
    NoteGroupEntity(titleEn = "Work", titleFa = "کار", isDefault = true),
    NoteGroupEntity(titleEn = "Ideas", titleFa = "ایده‌ها", isDefault = true),
    NoteGroupEntity(titleEn = "Learning", titleFa = "یادگیری", isDefault = true),
    NoteGroupEntity(titleEn = "Recipes", titleFa = "آشپزی", isDefault = true),
)

private fun seedGroups(connection: SQLiteConnection) {
    val insert = connection.prepare("INSERT INTO note_groups (id, titleEn, titleFa, isDefault) VALUES (?, ?, ?, 1)")
    try {
        defaultNoteGroups.forEachIndexed { index, group ->
            insert.bindLong(1, index.toLong() + 1)
            insert.bindText(2, group.titleEn); insert.bindText(3, group.titleFa)
            insert.step(); insert.reset()
        }
    } finally { insert.close() }
}

object NoteDefaultsCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) = seedGroups(connection)
}
