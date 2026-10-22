package com.sepideh.lilo.password.domain.repository

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest

interface PasswordRepository {
    fun observePasswords(): kotlinx.coroutines.flow.Flow<List<Password>>
    suspend fun unlockRequest(id: String): PasswordUnlockRequest
    suspend fun get(id: String): Password?
    suspend fun save(password: Password): Password
    suspend fun delete(id: String)
}
