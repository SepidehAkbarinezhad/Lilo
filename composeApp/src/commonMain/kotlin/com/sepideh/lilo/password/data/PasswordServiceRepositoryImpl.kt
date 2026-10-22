package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.password.domain.repository.PasswordServiceRepository

import com.sepideh.lilo.password.data.local.*
import kotlinx.coroutines.flow.map
import com.sepideh.lilo.core.domain.validation.requireShortNameLength
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PasswordServiceRepositoryImpl(private val dao: PasswordServiceDao) : PasswordServiceRepository {
    override fun observe() = dao.observe().map { rows -> rows.map { PasswordService(it.id, it.name, it.isDefault, it.nameFa) } }
    override suspend fun create(name: String): PasswordService {
        val clean = name.trim(); require(clean.isNotEmpty()); requireShortNameLength(clean)
        val row = PasswordServiceEntity(Uuid.random().toString(), clean, clean.lowercase(), false)
        dao.insertUnique(row)
        return PasswordService(row.id, row.name)
    }
    override suspend fun rename(id: String, name: String) {
        val clean = name.trim(); require(clean.isNotEmpty()); requireShortNameLength(clean)
        require(dao.get(id)?.isDefault == false)
        dao.renameUnique(id, clean, clean.lowercase())
    }
    override suspend fun usage(id: String) = dao.usage(id)
    override suspend fun delete(id: String, replacementId: String?) = dao.delete(id, replacementId)
}
