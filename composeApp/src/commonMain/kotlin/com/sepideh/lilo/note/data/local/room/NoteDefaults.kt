package com.sepideh.lilo.note.data.local.room

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

internal val defaultNoteGroups = listOf(
    NoteGroupEntity(titleEn = "Personal", titleFa = "شخصی", isDefault = true),
    NoteGroupEntity(titleEn = "Work", titleFa = "کار", isDefault = true),
    NoteGroupEntity(titleEn = "Ideas", titleFa = "ایده‌ها", isDefault = true),
    NoteGroupEntity(titleEn = "Learning", titleFa = "یادگیری", isDefault = true),
    NoteGroupEntity(titleEn = "Recipes", titleFa = "آشپزی", isDefault = true),
)

private fun seedGroups(connection: SQLiteConnection) {
    // Never accidentally associate legacy note group IDs with newly seeded groups.
    val max = connection.prepare("SELECT COALESCE(MAX(groupId), 0) FROM notes")
    val firstId = try { max.step(); max.getLong(0) + 1 } finally { max.close() }
    val insert = connection.prepare("INSERT INTO note_groups (id, titleEn, titleFa, isDefault) VALUES (?, ?, ?, 1)")
    try {
        defaultNoteGroups.forEachIndexed { index, group ->
            insert.bindLong(1, firstId + index)
            insert.bindText(2, group.titleEn); insert.bindText(3, group.titleFa)
            insert.step(); insert.reset()
        }
    } finally { insert.close() }
}

object NoteDefaultsCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) = seedGroups(connection)
}

val NoteFeatureMigration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE notes ADD COLUMN imageNames TEXT NOT NULL DEFAULT '[]'")
        connection.execSQL("CREATE TABLE IF NOT EXISTS note_groups (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titleEn TEXT NOT NULL, titleFa TEXT NOT NULL, isDefault INTEGER NOT NULL)")
        seedGroups(connection)
    }
}
