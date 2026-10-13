package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.data.local.*
import com.sepideh.lilo.password.domain.*
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PasswordServiceRepositoryImpl(private val dao: PasswordServiceDao) : PasswordServiceRepository {
    // Keep the persisted built-in ID/name so existing password links and uniqueness keys stay intact.
    override fun observe() = dao.observe().map { rows -> rows.map { PasswordService(it.id, if (it.isDefault && it.id == "google") "Gmail" else it.name, it.isDefault) } }
    override suspend fun create(name: String): PasswordService {
        val clean = name.trim(); require(clean.isNotEmpty())
        val row = PasswordServiceEntity(Uuid.random().toString(), clean, clean.lowercase(), false)
        dao.insertUnique(row)
        return PasswordService(row.id, row.name)
    }
    override suspend fun rename(id: String, name: String) {
        val clean = name.trim(); require(clean.isNotEmpty())
        require(dao.get(id)?.isDefault == false)
        dao.renameUnique(id, clean, clean.lowercase())
    }
    override suspend fun usage(id: String) = dao.usage(id)
    override suspend fun delete(id: String, replacementId: String?) = dao.delete(id, replacementId)
}
