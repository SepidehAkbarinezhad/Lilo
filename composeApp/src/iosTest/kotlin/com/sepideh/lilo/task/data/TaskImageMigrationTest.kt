package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.data.local.room.TaskImageMigration
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlin.test.*

/** Bundled SQLite is native on iOS; Android's JNI driver requires an instrumented device. */
class TaskImageMigrationTest {
    @Test fun migrationRetainsExistingRowsAndInitializesEmptyAttachments() {
        val connection = BundledSQLiteDriver().open(":memory:")
        try {
            connection.execSQL("CREATE TABLE TaskEntity (id INTEGER PRIMARY KEY, title TEXT NOT NULL)")
            connection.execSQL("INSERT INTO TaskEntity VALUES (1, 'Existing task')")
            TaskImageMigration.migrate(connection)
            val statement = connection.prepare("SELECT title, imageNames FROM TaskEntity WHERE id = 1")
            try {
                assertTrue(statement.step())
                assertEquals("Existing task", statement.getText(0))
                assertEquals("[]", statement.getText(1))
            } finally { statement.close() }
        } finally { connection.close() }
    }
}
