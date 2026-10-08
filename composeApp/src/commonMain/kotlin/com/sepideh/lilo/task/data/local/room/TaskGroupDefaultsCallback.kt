package com.sepideh.lilo.task.data.local.room

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection

/** Seed once when the database is created; deleting groups never recreates them. */
object TaskGroupDefaultsCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        val insert = connection.prepare("INSERT INTO task_groups (titleEn, titleFa, isDefault) VALUES (?, ?, ?)")
        try {
            defaultTaskGroups.forEach { group ->
                insert.bindText(1, group.titleEn)
                insert.bindText(2, group.titleFa)
                insert.bindLong(3, 1)
                insert.step()
                insert.reset()
            }
        } finally { insert.close() }
    }
}
