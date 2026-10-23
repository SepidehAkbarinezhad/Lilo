package com.sepideh.lilo.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.useReaderConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import com.sepideh.lilo.task.data.local.room.*
import com.sepideh.lilo.task.data.repository.TaskRepoImpl
import com.sepideh.lilo.task.data.repository.TaskGroupRepositoryImpl
import com.sepideh.lilo.task.domain.TaskGroup
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.note.data.local.room.*
import com.sepideh.lilo.password.data.local.*
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.test.*

/** Real Room-generated DAOs and bundled SQLite, not in-memory repository fakes. */
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrityTest {
    private val context: Context get() = ApplicationProvider.getApplicationContext()
    private fun taskDb(name: String) = Room.databaseBuilder<TaskDatabase>(context, name)
        .setDriver(BundledSQLiteDriver()).addCallback(TaskGroupDefaultsCallback).build()
    private fun noteDb(name: String) = Room.databaseBuilder<NoteDatabase>(context, name)
        .setDriver(BundledSQLiteDriver()).addCallback(NoteDefaultsCallback).build()
    private fun passwordDb(name: String) = Room.databaseBuilder<PasswordDatabase>(context, name)
        .setDriver(BundledSQLiteDriver()).addCallback(PasswordDefaultsCallback).build()

    @Test fun taskGroupsAreAtomicAndUpsertKeepsIdentityAcrossReopen() = runBlocking<Unit> {
        val name = "test-tasks-${UUID.randomUUID()}.db"
        var db = taskDb(name)
        try {
            val groups = TaskGroupRepositoryImpl(db.taskGroupDao())
            val tasks = TaskRepoImpl(db.taskDao())
            assertEquals(6, groups.getAllGroups().first().size)
            val groupId = groups.addGroup(TaskGroup(titleEn = "Custom", titleFa = "سفارشی"))
            val id = tasks.upsertTask(Task(title = "Original", groupId = groupId, imageNames = listOf("kept.jpg")))
            assertEquals(1, tasks.getTasksByFilter(null, emptyList()).first().size)
            assertEquals(0, tasks.getTasksByFilter(true, emptyList()).first().size)
            assertEquals(0, tasks.getTasksByFilter(null, listOf(2)).first().size)
            val saved = assertNotNull(tasks.getTaskById(id))
            assertEquals(id, tasks.upsertTask(saved.copy(title = "Edited")))
            assertEquals(groupId, groups.addGroup(TaskGroup(groupId, "Renamed", "سفارشی")))
            assertEquals(groupId, tasks.getTaskById(id)?.groupId)
            assertFailsWith<DuplicateNameException> { groups.addGroup(TaskGroup(titleEn = " renamed ")) }
            groups.deleteGroup(groupId)
            val ungrouped = assertNotNull(tasks.getTaskById(id))
            assertEquals(0L, ungrouped.groupId)
            assertEquals(listOf("kept.jpg"), ungrouped.imageNames)
            assertEquals(saved.createdAt, ungrouped.createdAt)
            // A stale form must not recreate an orphan group assignment.
            assertEquals(id, tasks.upsertTask(ungrouped.copy(groupId = groupId)))
            assertEquals(0L, tasks.getTaskById(id)?.groupId)
            assertFails { db.taskDao().upsert(taskRow(groupId = groupId)) }
            val defaultId = groups.getAllGroups().first().first().id
            groups.deleteGroup(defaultId)
            checkIntegrity(db)
            db.close()
            db = taskDb(name)
            assertEquals("Edited", db.taskDao().getTaskById(id)?.title)
            assertEquals(5, db.taskGroupDao().getAllGroups().first().size)
            checkIntegrity(db)
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun concurrentDuplicateGroupCreationCommitsOnlyOneRow() = runBlocking<Unit> {
        val name = "test-duplicates-${UUID.randomUUID()}.db"
        val db = taskDb(name)
        try {
            val results = coroutineScope {
                List(2) { async(Dispatchers.IO) {
                    runCatching { db.taskGroupDao().upsertUnique(TaskGroupEntity(titleEn = "Same", titleFa = "")) }
                } }.awaitAll()
            }
            assertEquals(1, results.count { it.isSuccess })
            assertEquals(1, results.count { it.exceptionOrNull() is DuplicateNameException })
            assertEquals(1, db.taskGroupDao().getAllGroups().first().count { it.titleEn == "Same" })
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun notesKeepContentAndImagesWhenGroupIsDeletedAndReopenDoesNotReseed() = runBlocking<Unit> {
        val name = "test-notes-${UUID.randomUUID()}.db"
        var db = noteDb(name)
        try {
            val group = db.noteGroupDao().getAllGroups().first().first()
            val note = NoteEntity(title = "یادداشت", content = "Body", groupId = group.id,
                createdAt = 10, updatedAt = 20, imageNames = "[\"kept.jpg\"]")
            val id = db.noteDao().upsertWithGroup(note)
            assertEquals(id, db.noteDao().upsertWithGroup(note.copy(id = id, content = "Edited")))
            // Raw deletion is safe as well: SET NULL is enforced by SQLite.
            db.noteGroupDao().deleteById(group.id)
            val saved = assertNotNull(db.noteDao().getNoteById(id))
            assertNull(saved.groupId)
            assertEquals("Edited", saved.content)
            assertEquals(note.imageNames, saved.imageNames)
            assertFails { db.noteDao().upsertNote(note) }
            assertEquals(id, db.noteDao().upsertWithGroup(note.copy(id = id)))
            assertNull(db.noteDao().getNoteById(id)?.groupId)
            checkIntegrity(db)
            db.close()
            db = noteDb(name)
            assertEquals(4, db.noteGroupDao().getAllGroups().first().size)
            assertEquals(note.imageNames, db.noteDao().getNoteById(id)?.imageNames)
            checkIntegrity(db)
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun passwordServiceMovesAreAtomicAndNeverChangeCiphertext() = runBlocking<Unit> {
        val name = "test-passwords-${UUID.randomUUID()}.db"
        var db = passwordDb(name)
        try {
            assertEquals(7, db.services().observe().first().size)
            db.services().insertUnique(PasswordServiceEntity("custom", "Custom", "custom", false))
            val record = PasswordEntity("credential", PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, byteArrayOf(1), byteArrayOf(2, 3),
                wrappedKey = byteArrayOf(4), title = "Mail", createdAt = 10, updatedAt = 20, serviceId = "custom")
            db.passwords().saveWithService(record)
            assertFails { db.passwords().save(record.copy(id = "orphan", serviceId = "missing")) }
            assertFails { db.services().delete("custom", "missing") }
            assertEquals("custom", db.passwords().get(record.id)?.serviceId)
            assertFails { db.services().delete("google", "custom") }
            db.services().delete("custom", "google")
            val moved = assertNotNull(db.passwords().get(record.id))
            assertEquals("google", moved.serviceId)
            assertContentEquals(record.ciphertext, moved.ciphertext)
            assertContentEquals(record.wrappedKey, moved.wrappedKey)
            assertContentEquals(record.nonce, moved.nonce)
            assertEquals(record.createdAt, moved.createdAt)
            assertEquals("Mail", db.passwords().observe().first().single().title)
            checkIntegrity(db)
            db.close()
            db = passwordDb(name)
            assertContentEquals(record.ciphertext, db.passwords().get(record.id)?.ciphertext)
            assertNull(db.services().get("custom"))
            checkIntegrity(db)
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun unsupportedDevelopmentDatabaseFailsWithoutDeletingExistingData() = runBlocking<Unit> {
        val name = "test-old-schema-${UUID.randomUUID()}.db"
        context.openOrCreateDatabase(name, 0, null).use { sqlite ->
            sqlite.execSQL("CREATE TABLE preserved (value TEXT NOT NULL)")
            sqlite.execSQL("INSERT INTO preserved VALUES ('keep me')")
            sqlite.version = 2
        }
        val db = noteDb(name)
        try {
            assertFails { db.noteDao().getAllNotes().first() }
        } finally { db.close() }
        try {
            context.openOrCreateDatabase(name, 0, null).use { sqlite ->
                sqlite.rawQuery("SELECT value FROM preserved", null).use { row ->
                    assertTrue(row.moveToFirst())
                    assertEquals("keep me", row.getString(0))
                }
                assertEquals(2, sqlite.version)
            }
        } finally { context.deleteDatabase(name) }
    }

    private suspend fun checkIntegrity(db: RoomDatabase) {
        db.useReaderConnection { connection ->
            connection.usePrepared("PRAGMA foreign_keys") { row -> assertTrue(row.step()); assertEquals(1L, row.getLong(0)) }
            connection.usePrepared("PRAGMA foreign_key_check") { row -> assertFalse(row.step()) }
            connection.usePrepared("PRAGMA integrity_check") { row -> assertTrue(row.step()); assertEquals("ok", row.getText(0)) }
            connection.usePrepared("PRAGMA user_version") { row -> assertTrue(row.step()); assertEquals(1L, row.getLong(0)) }
        }
    }

    private fun taskRow(groupId: Long?) = TaskEntity(title = "Task", description = "", groupId = groupId, createdAt = 1, updatedAt = 1)
}
