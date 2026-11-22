package com.sepideh.lilo.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sepideh.lilo.task.data.local.room.*
import com.sepideh.lilo.task.data.mapper.toEntity
import com.sepideh.lilo.task.data.mapper.toTask
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import kotlin.test.*
import kotlin.time.Instant
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MonthlyReminderMigrationTest {
    @Test fun version1MigrationPreservesEveryExistingReminderAndCanStoreMonthly() = runBlocking<Unit> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "test-monthly-migration-${UUID.randomUUID()}.db"
        val at = Instant.parse("2026-01-31T09:15:00Z").toEpochMilliseconds()
        val originals = listOf(RepeatRule.NONE, RepeatRule.DAILY, RepeatRule.WEEKLY).mapIndexed { index, rule ->
            Task(id = index + 1L, title = "Keep $rule", description = "Body", groupId = 7, priority = index,
                reminderAt = at, repeatRule = rule, reminderTimeZoneId = "UTC", createdAt = 10, updatedAt = 20,
                done = index == 2, completedAt = if (index == 2) 30 else null, imageNames = listOf("kept.jpg"))
        }
        context.openOrCreateDatabase(name, 0, null).use { sqlite ->
            // Frozen version-1 schema, including Room identity and foreign key/index definitions.
            sqlite.execSQL("CREATE TABLE IF NOT EXISTS `TaskEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `done` INTEGER NOT NULL, `groupId` INTEGER, `priority` INTEGER NOT NULL, `reminderAt` INTEGER, `repeatRule` TEXT NOT NULL, `reminderTimeZoneId` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `completedAt` INTEGER, `imageNames` TEXT NOT NULL DEFAULT '[]', FOREIGN KEY(`groupId`) REFERENCES `task_groups`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
            sqlite.execSQL("CREATE INDEX IF NOT EXISTS `index_TaskEntity_groupId` ON `TaskEntity` (`groupId`)")
            sqlite.execSQL("CREATE TABLE IF NOT EXISTS `task_groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `titleEn` TEXT NOT NULL, `titleFa` TEXT NOT NULL, `isDefault` INTEGER NOT NULL)")
            sqlite.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
            sqlite.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'af6faf3c7fe5890de8d066bd231dc6c5')")
            sqlite.execSQL("INSERT INTO task_groups VALUES (7, 'Custom', 'سفارشی', 0)")
            originals.forEach { task ->
                sqlite.execSQL("INSERT INTO TaskEntity (id, title, description, done, groupId, priority, reminderAt, repeatRule, reminderTimeZoneId, createdAt, updatedAt, completedAt, imageNames) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    arrayOf<Any?>(task.id, task.title, task.description, if (task.done) 1 else 0, task.groupId,
                        task.priority, task.reminderAt, task.repeatRule.name, task.reminderTimeZoneId,
                        task.createdAt, task.updatedAt, task.completedAt, task.toEntity().imageNames))
            }
            sqlite.version = 1
        }
        fun open() = Room.databaseBuilder<TaskDatabase>(context, name).setDriver(BundledSQLiteDriver())
            .addMigrations(TASK_MIGRATION_1_2).addCallback(TaskGroupDefaultsCallback).build()
        var db = open()
        try {
            // Opening runs the migration and Room's validation against the new generated schema.
            originals.forEach { task ->
                val row = assertNotNull(db.taskDao().getTaskById(task.id!!))
                assertEquals(task, row.toTask())
                assertNull(row.reminderCalendar)
                assertNull(row.reminderLocalDateTime)
            }
            assertEquals(listOf(7L), db.taskGroupDao().getAllGroups().first().map { it.id })
            for (calendar in ReminderCalendarType.entries) {
                val monthly = originals.first().copy(id = 10L + calendar.ordinal, repeatRule = RepeatRule.MONTHLY,
                    monthlyAnchor = MonthlyAnchor(calendar, LocalDateTime.parse("2026-01-31T09:15")))
                db.taskDao().upsert(monthly.toEntity())
                assertEquals(monthly, db.taskDao().getTaskById(monthly.id!!)!!.toTask())
            }
            db.close()
            db = open()
            originals.forEach { assertEquals(it, db.taskDao().getTaskById(it.id!!)!!.toTask()) }
            assertEquals(ReminderCalendarType.JALALI, db.taskDao().getTaskById(11)!!.toTask().monthlyAnchor!!.calendar)
            db.taskGroupDao().deleteById(7)
            assertNull(db.taskDao().getTaskById(1)!!.groupId) // SET NULL foreign key still works.
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
