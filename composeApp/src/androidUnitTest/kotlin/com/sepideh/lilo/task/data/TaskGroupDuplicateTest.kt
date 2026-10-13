package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.data.local.room.*
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class TaskGroupDuplicateTest {
    @Test fun duplicateCreationAndRenameAreRejectedWhileOwnNameIsAllowed() = runBlocking<Unit> {
        val dao = MemoryGroups()
        for (name in listOf("Work", " work ")) {
            assertFailsWith<DuplicateNameException> { dao.upsertUnique(TaskGroupEntity(titleEn = name, titleFa = "")) }
        }
        assertFailsWith<DuplicateNameException> { dao.upsertUnique(TaskGroupEntity(titleEn = "", titleFa = " کاری ")) }
        assertFailsWith<DuplicateNameException> { dao.upsertUnique(TaskGroupEntity(id = 2, titleEn = "Work", titleFa = "")) }
        assertEquals(2, dao.rows.size)
        assertEquals("Personal", dao.getGroupById(2)?.titleEn)
        dao.upsertUnique(TaskGroupEntity(id = 2, titleEn = "Personal", titleFa = ""))
        dao.upsertUnique(TaskGroupEntity(titleEn = "Shopping", titleFa = ""))
        assertEquals(3, dao.rows.size)
    }

    private class MemoryGroups : TaskGroupDao {
        val rows = mutableListOf(
            TaskGroupEntity(1, "Work", "کاری", true),
            TaskGroupEntity(2, "Personal", "", false),
        )
        override suspend fun getGroupSnapshot() = rows.toList()
        override fun getAllGroups() = flowOf(rows.toList())
        override suspend fun getGroupById(groupId: Long) = rows.find { it.id == groupId }
        override suspend fun deleteById(id: Long) { rows.removeAll { it.id == id } }
        override suspend fun upsert(group: TaskGroupEntity): Long {
            val saved = if (group.id == 0L) group.copy(id = (rows.maxOfOrNull { it.id } ?: 0) + 1) else group
            rows.removeAll { it.id == saved.id }
            rows += saved
            return saved.id
        }
    override suspend fun delete(group: TaskGroupEntity) { rows.removeAll { it.id == group.id } }
    }
}
