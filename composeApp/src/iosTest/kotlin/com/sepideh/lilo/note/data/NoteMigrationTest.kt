package com.sepideh.lilo.note.data
import com.sepideh.lilo.note.data.local.room.NoteFeatureMigration
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlin.test.*
class NoteMigrationTest {
 @Test fun upgradePreservesNotesAndAvoidsReusingLegacyGroupIds() {
  val db=BundledSQLiteDriver().open(":memory:")
  try {
   db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY, title TEXT NOT NULL, content TEXT NOT NULL, groupId INTEGER, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
   db.execSQL("INSERT INTO notes VALUES (1,'Existing','Body',7,10,20)")
   NoteFeatureMigration.migrate(db)
   val row=db.prepare("SELECT title,content,groupId,createdAt,updatedAt,imageNames FROM notes")
   try { assertTrue(row.step());assertEquals("Existing",row.getText(0));assertEquals("Body",row.getText(1));assertEquals(7L,row.getLong(2));assertEquals(10L,row.getLong(3));assertEquals(20L,row.getLong(4));assertEquals("[]",row.getText(5)) } finally { row.close() }
   val groups=db.prepare("SELECT MIN(id),COUNT(*) FROM note_groups WHERE isDefault=1 AND titleEn<>'' AND titleFa<>''")
   try { assertTrue(groups.step());assertTrue(groups.getLong(0)>7);assertEquals(5L,groups.getLong(1)) } finally { groups.close() }
  } finally { db.close() }
 }
}
