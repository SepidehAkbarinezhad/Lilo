package com.sepideh.lilo.note.data

import com.sepideh.lilo.note.data.local.room.*
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class NoteGroupDuplicateTest {
    @Test fun duplicateCreationAndRenameAreRejectedWhileOwnNameIsAllowed() = runBlocking<Unit> {
        val dao = MemoryGroups()
        for (name in listOf("Work", " work ")) {
            assertFailsWith<DuplicateNameException> { dao.upsertUnique(NoteGroupEntity(titleEn = name, titleFa = "")) }
        }
        assertFailsWith<DuplicateNameException> { dao.upsertUnique(NoteGroupEntity(titleEn = "", titleFa = " کاری ")) }
        assertFailsWith<DuplicateNameException> { dao.upsertUnique(NoteGroupEntity(id = 2, titleEn = "Work", titleFa = "")) }
        assertEquals(2, dao.rows.size)
        assertEquals("Personal", dao.getGroupById(2)?.titleEn)
        dao.upsertUnique(NoteGroupEntity(id = 2, titleEn = "Personal", titleFa = ""))
        dao.upsertUnique(NoteGroupEntity(titleEn = "Shopping", titleFa = ""))
        assertEquals(3, dao.rows.size)
    }

    private class MemoryGroups : NoteGroupDao {
        val rows = mutableListOf(
            NoteGroupEntity(1, "Work", "کاری", true),
            NoteGroupEntity(2, "Personal", "", false),
        )
        override suspend fun getGroupSnapshot() = rows.toList()
        override fun getAllGroups() = flowOf(rows.toList())
        override suspend fun getGroupById(id: Long) = rows.find { it.id == id }
        override suspend fun deleteById(id: Long) { rows.removeAll { it.id == id } }
        override suspend fun upsert(group: NoteGroupEntity): Long {
            val saved = if (group.id == 0L) group.copy(id = (rows.maxOfOrNull { it.id } ?: 0) + 1) else group
            rows.removeAll { it.id == saved.id }
            rows += saved
            return saved.id
        }
    override suspend fun clearGroup(id: Long) = Unit
    }
}
