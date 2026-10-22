package com.sepideh.lilo.password.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sepideh.lilo.password.domain.model.PasswordService

interface PasswordServiceRepository {
    fun observe(): Flow<List<PasswordService>>
    suspend fun create(name: String): PasswordService
    suspend fun rename(id: String, name: String)
    suspend fun usage(id: String): Int
    suspend fun delete(id: String, replacementId: String?)
}
