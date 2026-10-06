package com.sepideh.lilo.task.data.local.room

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val TaskImageMigration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE TaskEntity ADD COLUMN imageNames TEXT NOT NULL DEFAULT '[]'")
    }
}
