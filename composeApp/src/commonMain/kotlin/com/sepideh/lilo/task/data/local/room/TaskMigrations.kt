package com.sepideh.lilo.task.data.local.room

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/** Existing Once/Daily/Weekly reminders keep their original instant, zone and repeat rule. */
val TASK_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE TaskEntity ADD COLUMN reminderCalendar TEXT")
        connection.execSQL("ALTER TABLE TaskEntity ADD COLUMN reminderLocalDateTime TEXT")
    }
}
