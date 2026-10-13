package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.*
import kotlinx.coroutines.*
import kotlin.test.*

class SessionVaultCryptoTest {
    @Test fun consumesKeyAndClearsOperationCopiesOnLock() = runBlocking<Unit> {
        val vault = ProbeCrypto()
        val key = ByteArray(32) { 7 }
        vault.unlock(key)
        assertTrue(key.all { it == 0.toByte() })
        val generation = vault.generation
        vault.seal(byteArrayOf(1), "id")
        assertTrue(vault.operationKey!!.all { it == 0.toByte() })
        vault.lock()
        assertFalse(vault.unlocked)
        assertTrue(vault.generation > generation)
        assertFailsWith<VaultLockedException> { vault.seal(byteArrayOf(1), "id") }
        assertFailsWith<VaultLockedException> { vault.open(SealedPassword(1, byteArrayOf(), byteArrayOf(1)), "id") }
    }
    @Test fun lockDuringDecryptionRejectsAndWipesLatePlaintext() = runBlocking<Unit> {
        val vault = ProbeCrypto()
        vault.unlock(ByteArray(32) { 7 })
        val started = CompletableDeferred<Unit>()
        val resume = CompletableDeferred<Unit>()
        vault.pause = { started.complete(Unit); resume.await() }
        val result = async { runCatching { vault.open(SealedPassword(1, byteArrayOf(), byteArrayOf(1)), "id") } }
        started.await()
        vault.lock()
        vault.unlock(ByteArray(32) { 8 })
        resume.complete(Unit)
        assertIs<VaultLockedException>(result.await().exceptionOrNull())
        assertTrue(vault.decrypted!!.all { it == 0.toByte() })
        assertTrue(vault.operationKey!!.all { it == 0.toByte() })
    }
    @Test fun authenticationForOneRecordDoesNotAuthorizeAnother() = runBlocking<Unit> {
        val vault = ProbeCrypto()
        vault.unlock(ByteArray(32) { 7 }, "one")
        assertFailsWith<VaultLockedException> { vault.open(SealedPassword(1, byteArrayOf(), byteArrayOf(1)), "two") }
        assertContentEquals(byteArrayOf(1, 2, 3), vault.open(SealedPassword(1, byteArrayOf(), byteArrayOf(1)), "one"))
    }
    private class ProbeCrypto : SessionVaultCrypto() {
        var operationKey: ByteArray? = null
        var decrypted: ByteArray? = null
        var pause: suspend () -> Unit = {}
        fun unlock(key: ByteArray, id: String? = null) = acceptAuthenticatedKey(key, id)
        override suspend fun encrypt(key: ByteArray, plaintext: ByteArray, aad: ByteArray): SealedPassword {
            operationKey = key
            return SealedPassword(1, byteArrayOf(), plaintext.copyOf())
        }
        override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray {
            operationKey = key
            pause()
            return byteArrayOf(1, 2, 3).also { decrypted = it }
        }
    }
}
