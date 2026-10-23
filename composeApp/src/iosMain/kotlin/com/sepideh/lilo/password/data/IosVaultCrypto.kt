package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.sepideh.lilo.password.domain.crypto.UnlockResult
import com.sepideh.lilo.password.domain.crypto.VaultKeyUnavailableException
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.LocalAuthentication.*
import platform.Security.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
internal fun cfObject(pointer: CFTypeRef?): Any = requireNotNull(CFBridgingRelease(CFRetain(pointer)))
@OptIn(ExperimentalForeignApi::class)
internal fun <T> withDictionary(values: Map<Any?, Any?>, block: (CFDictionaryRef) -> T): T {
    val dictionary: CFDictionaryRef = requireNotNull(CFBridgingRetain(values)).reinterpret()
    return try { block(dictionary) } finally { CFRelease(dictionary) }
}

@OptIn(ExperimentalForeignApi::class)
fun passwordDirectory(): String {
    val manager = NSFileManager.defaultManager
    val root = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true).first() as String
    val path = "$root/LiloPasswords"
    check(manager.createDirectoryAtPath(path, true, mapOf(NSFileProtectionKey to NSFileProtectionComplete), null))
    val marker = "$path/install.marker"
    if (!manager.fileExistsAtPath(marker)) {
        // Keychain can outlive uninstall; a fresh local install must not reuse the old key.
        withDictionary(IosRecordKeys.query()) { SecItemDelete(it) }
        check(manager.createFileAtPath(marker, NSData(), null))
    }
    check(NSURL.fileURLWithPath(path).setResourceValue(true, NSURLIsExcludedFromBackupKey, null))
    return path
}

@OptIn(ExperimentalForeignApi::class)
class IosVaultCrypto : SessionVaultCrypto() {
    private val writes = Mutex()
    private var authentication: LAContext? = null
    private var attempt = 0
    fun cancelUnlock() { attempt++; authentication?.invalidate(); authentication = null }

    suspend fun unlock(reason: String, request: PasswordUnlockRequest): UnlockResult {
        lock()
        val context = LAContext()
        if (!context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, null)) { context.invalidate(); return UnlockResult.UNAVAILABLE }
        val token = ++attempt
        authentication = context
        var pendingKey: ByteArray? = null
        return try {
            require(request.id.isNotBlank())
            IosPasswordCipher.validateRecord(request.sealed)
            passwordDirectory()
            val key = withContext(Dispatchers.Default) {
                val bytes = IosRecordKeys.unwrap(request.sealed.wrappedKey, context, reason)
                bytes?.also { pendingKey = it; require(it.size == 64) }
            }
            pendingKey = key
            if (key == null) UnlockResult.CANCELLED
            else withContext(Dispatchers.Main.immediate) {
                if (token != attempt || UIApplication.sharedApplication.applicationState != UIApplicationState.UIApplicationStateActive) {
                    key.fill(0); UnlockResult.CANCELLED
                } else { acceptAuthenticatedKey(key, request.id); UnlockResult.SUCCESS }
            }
        } catch (e: CancellationException) { context.invalidate(); throw e }
        catch (_: VaultKeyUnavailableException) { UnlockResult.KEY_UNAVAILABLE }
        catch (_: Exception) { UnlockResult.FAILED }
        finally { pendingKey?.fill(0); context.invalidate(); if (authentication === context) authentication = null }
    }

    override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword = withContext(Dispatchers.Default) {
        writes.withLock {
            require(recordId.isNotBlank())
            val key = randomBytes(64)
            try {
                val wrapped = IosRecordKeys.wrap(key)
                IosPasswordCipher.encrypt(key, plaintext, "lilo-password-v1:$recordId".encodeToByteArray(), wrapped)
            } finally { key.fill(0) }
        }
    }

    override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray =
        IosPasswordCipher.decrypt(key, record, aad)
    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also { data ->
        data.usePinned { check(SecRandomCopyBytes(kSecRandomDefault, size.toULong(), it.addressOf(0)) == errSecSuccess) }
    }
}
