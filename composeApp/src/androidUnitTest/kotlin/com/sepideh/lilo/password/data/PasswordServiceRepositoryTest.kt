package com.sepideh.lilo.password.data

import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import com.sepideh.lilo.password.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class PasswordServiceRepositoryTest {
    @Test fun builtInGoogleAppearsAsGmailWithoutChangingPasswordLinksOrCustomNames() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        dao.assignments["password"] = "google"
        val custom = repo.create("Google workspace")
        val services = repo.observe().first()
        val gmail = services.single { it.id == "google" }
        assertEquals("Gmail", gmail.name)
        assertTrue(gmail.isDefault)
        assertEquals("google", dao.assignments["password"])
        assertEquals("Google", dao.get("google")?.name)
        assertEquals("Google workspace", services.single { it.id == custom.id }.name)
    }

    @Test fun customServicesCanBeRenamedButDefaultsCannot() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        val custom = repo.create("  My bank  ")
        assertEquals("My bank", custom.name)
        assertFalse(custom.isDefault)
        dao.assignments["password"] = custom.id
        repo.rename(custom.id, "Bank")
        assertEquals("Bank", dao.get(custom.id)?.name)
        assertEquals(custom.id, dao.assignments["password"])
        assertFailsWith<IllegalArgumentException> { repo.rename("google", "Something else") }
        assertFailsWith<IllegalArgumentException> { repo.create("GOOGLE") }
        assertEquals("Google", dao.get("google")?.name)
    }
    @Test fun deletingUsedCustomServiceRequiresExistingReplacementAndKeepsRecords() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        val custom = repo.create("Personal")
        dao.assignments["first"] = custom.id
        dao.assignments["second"] = custom.id
        dao.assignments["unrelated"] = "google"
        assertFailsWith<IllegalArgumentException> { repo.delete(custom.id, null) }
        assertFailsWith<IllegalArgumentException> { repo.delete(custom.id, custom.id) }
        assertFailsWith<IllegalArgumentException> { repo.delete(custom.id, "missing") }
        assertNotNull(dao.get(custom.id))
        assertEquals(custom.id, dao.assignments["first"])
        assertEquals(custom.id, dao.assignments["second"])
        val replacement = repo.create("Work")
        repo.delete(custom.id, replacement.id)
        assertNull(dao.get(custom.id))
        assertEquals(mapOf("first" to replacement.id, "second" to replacement.id, "unrelated" to "google"), dao.assignments)
    }
    @Test fun builtInServiceCannotBeDeletedWithOrWithoutPasswords() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        val replacement = repo.create("Work")
        assertFailsWith<IllegalArgumentException> { repo.delete("google", null) }
        assertFailsWith<IllegalArgumentException> { repo.delete("google", replacement.id) }
        dao.assignments["password"] = "google"
        assertFailsWith<IllegalArgumentException> { repo.delete("google", replacement.id) }
        assertNotNull(dao.get("google"))
        assertEquals("google", dao.assignments["password"])
    }
    @Test fun customServiceCanMovePasswordsToBuiltInService() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        val custom = repo.create("Personal")
        dao.assignments["password"] = custom.id
        repo.delete(custom.id, "google")
        assertNull(dao.get(custom.id))
        assertEquals("google", dao.assignments["password"])
        assertNotNull(dao.get("google"))
    }
    @Test fun unusedCustomServiceCanBeDeletedWithoutReplacement() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        val custom = repo.create("Personal")
        repo.delete(custom.id, null)
        assertNull(dao.get(custom.id))
        assertNotNull(dao.get("google"))
    }
    @Test fun duplicateNamesAndGmailAliasAreRejectedWithoutChangingServices() = runBlocking<Unit> {
        val dao = MemoryServices()
        val repo = PasswordServiceRepositoryImpl(dao)
        val custom = repo.create("Work")
        assertFailsWith<DuplicateNameException> { repo.create(" Work ") }
        assertFailsWith<DuplicateNameException> { repo.create("work") }
        assertFailsWith<DuplicateNameException> { repo.create(" Gmail ") }
        assertFailsWith<DuplicateNameException> { repo.rename(custom.id, "GMAIL") }
        assertEquals("Work", dao.get(custom.id)?.name)
        repo.rename(custom.id, " Work ")
        assertEquals(2, repo.observe().first().size)
    }
    private class MemoryServices : PasswordServiceDao() {
        private val rows = MutableStateFlow(listOf(PasswordServiceEntity("google", "Google", "google", true)))
        val assignments = mutableMapOf<String, String>()
        override suspend fun getServiceSnapshot() = rows.value
        override fun observe() = rows
        override suspend fun get(id: String) = rows.value.find { it.id == id }
        override suspend fun insert(row: PasswordServiceEntity) {
            require(rows.value.none { it.nameKey == row.nameKey })
            rows.value += row
        }
        override suspend fun rename(id: String, name: String, key: String) {
            require(rows.value.none { it.id != id && it.nameKey == key })
            rows.value = rows.value.map { if (it.id == id && !it.isDefault) it.copy(name = name, nameKey = key) else it }
        }
        override suspend fun usage(id: String) = assignments.values.count { it == id }
        override suspend fun move(id: String, replacement: String) {
            assignments.keys.toList().forEach { if (assignments[it] == id) assignments[it] = replacement }
        }
        override suspend fun remove(id: String) { rows.value = rows.value.filterNot { it.id == id } }
    }
}
