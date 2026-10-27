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
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun <T> withDictionary(values: Map<*, *>, block: (CFDictionaryRef) -> T): T = memScoped {
    // Kotlin Boolean bridges as NSNumber, but Security requires CFBoolean for flags
    // such as kSecReturnRef and the nested kSecAttrIsPermanent attribute.
    val dictionary = requireNotNull(CFDictionaryCreateMutable(kCFAllocatorDefault, values.size.toLong(),
        kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr))
    try {
        for ((key, value) in values) {
            val nativeKey = requireNotNull(CFBridgingRetain(key))
            try {
                when (value) {
                    is Boolean -> CFDictionarySetValue(dictionary, nativeKey, if (value) kCFBooleanTrue else kCFBooleanFalse)
                    is Map<*, *> -> withDictionary(value) { CFDictionarySetValue(dictionary, nativeKey, it) }
                    else -> {
                        val nativeValue = requireNotNull(CFBridgingRetain(value))
                        try { CFDictionarySetValue(dictionary, nativeKey, nativeValue) }
                        finally { CFRelease(nativeValue) }
                    }
                }
            } finally { CFRelease(nativeKey) }
        }
        block(dictionary)
    } finally { CFRelease(dictionary) }
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

// Called on Main: a system authentication sheet can leave the app Inactive briefly
// after Keychain returns. Only resume in Active; backgrounding/cancellation wins.
internal suspend fun awaitPasswordForeground(
    result: CompletableDeferred<Boolean>,
    center: NSNotificationCenter = NSNotificationCenter.defaultCenter,
    applicationState: () -> UIApplicationState = { UIApplication.sharedApplication.applicationState },
): Boolean {
    val active = center.addObserverForName(UIApplicationDidBecomeActiveNotification, null, NSOperationQueue.mainQueue) {
        result.complete(true)
    }
    val background = center.addObserverForName(UIApplicationDidEnterBackgroundNotification, null, NSOperationQueue.mainQueue) {
        result.complete(false)
    }
    return try {
        // Subscribe before inspecting state so activation cannot be missed.
        when (applicationState()) {
            UIApplicationState.UIApplicationStateActive -> result.complete(true)
            UIApplicationState.UIApplicationStateBackground -> result.complete(false)
            else -> Unit
        }
        result.await() && applicationState() == UIApplicationState.UIApplicationStateActive
    } finally {
        center.removeObserver(active)
        center.removeObserver(background)
    }
}

@OptIn(ExperimentalForeignApi::class)
class IosVaultCrypto : SessionVaultCrypto() {
    private val writes = Mutex()
    private var authentication: LAContext? = null
    private var attempt = 0
    private var foreground: CompletableDeferred<Boolean>? = null
    fun cancelUnlock() {
        attempt++
        authentication?.invalidate()
        authentication = null
        foreground?.complete(false)
        foreground = null
    }

    suspend fun unlock(reason: String, request: PasswordUnlockRequest): UnlockResult {
        cancelUnlock()
        lock()
        val context = LAContext()
        if (!context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, null)) { context.invalidate(); return UnlockResult.UNAVAILABLE }
        val token = ++attempt
        authentication = context
        val activation = CompletableDeferred<Boolean>()
        foreground = activation
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
                if (token != attempt || !awaitPasswordForeground(activation) || token != attempt) {
                    key.fill(0); UnlockResult.CANCELLED
                } else { acceptAuthenticatedKey(key, request.id); UnlockResult.SUCCESS }
            }
        } catch (e: CancellationException) { context.invalidate(); throw e }
        catch (_: VaultKeyUnavailableException) { UnlockResult.KEY_UNAVAILABLE }
        catch (failure: Exception) {
            NSLog("LiloPasswordAuth: unlock failed (%@)", failure::class.simpleName.orEmpty())
            UnlockResult.FAILED
        }
        finally {
            pendingKey?.fill(0)
            context.invalidate()
            if (authentication === context) authentication = null
            if (foreground === activation) foreground = null
        }
    }

    override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword = withContext(Dispatchers.Default) {
        writes.withLock {
            require(recordId.isNotBlank())
            val key = randomBytes(64)
            try {
                val wrapped = IosRecordKeys.wrap(key)
                IosPasswordCipher.encrypt(key, plaintext, passwordAad(recordId), wrapped)
            } finally { key.fill(0) }
        }
    }

    override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray =
        IosPasswordCipher.decrypt(key, record, aad)
    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also { data ->
        data.usePinned { check(SecRandomCopyBytes(kSecRandomDefault, size.toULong(), it.addressOf(0)) == errSecSuccess) }
    }
}
