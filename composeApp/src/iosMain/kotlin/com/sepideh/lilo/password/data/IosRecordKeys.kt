package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.*
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.LocalAuthentication.*
import platform.Security.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal object IosRecordKeys {
    private fun tag() = "com.sepideh.lilo.passwords.reader.rsa.v1".encodeToByteArray().asData()
    fun query(): Map<Any?, Any?> = mapOf(cfObject(kSecClass) to cfObject(kSecClassKey),
        cfObject(kSecAttrApplicationTag) to tag(), cfObject(kSecAttrKeyType) to cfObject(kSecAttrKeyTypeRSA))

    private fun ByteArray.asData(): NSData = usePinned { NSData.create(bytes = it.addressOf(0), length = size.toULong()) }
    private fun NSData.asBytes(): ByteArray = bytes!!.reinterpret<ByteVar>().readBytes(length.toInt())
    private fun <T> withData(bytes: ByteArray, block: (CFDataRef) -> T): T {
        val data: CFDataRef = requireNotNull(CFBridgingRetain(bytes.asData())).reinterpret()
        return try { block(data) } finally { CFRelease(data) }
    }

    fun wrap(key: ByteArray): ByteArray {
        val path = passwordDirectory() + "/reader-public.der"
        val context = LAContext()
        try { if (!context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, null)) throw DeviceLockRequiredException() }
        finally { context.invalidate() }
        val manager = NSFileManager.defaultManager
        var publicData = manager.contentsAtPath(path)
        if (publicData == null) {
            val exists = withDictionary(query() + mapOf(cfObject(kSecUseAuthenticationUI) to cfObject(kSecUseAuthenticationUIFail))) {
                SecItemCopyMatching(it, null)
            }
            if (exists != errSecItemNotFound) throw VaultKeyUnavailableException()
            val control = requireNotNull(SecAccessControlCreateWithFlags(kCFAllocatorDefault,
                kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly, kSecAccessControlUserPresence or kSecAccessControlPrivateKeyUsage, null))
            try {
                val attrs: Map<Any?, Any?> = mapOf(cfObject(kSecAttrKeyType) to cfObject(kSecAttrKeyTypeRSA), cfObject(kSecAttrKeySizeInBits) to 2048,
                    cfObject(kSecPrivateKeyAttrs) to mapOf(cfObject(kSecAttrIsPermanent) to true,
                        cfObject(kSecAttrApplicationTag) to tag(), cfObject(kSecAttrAccessControl) to cfObject(control)))
                val privateKey = requireNotNull(withDictionary(attrs) { SecKeyCreateRandomKey(it, null) })
                try {
                    val publicKey = requireNotNull(SecKeyCopyPublicKey(privateKey))
                    try {
                        publicData = CFBridgingRelease(SecKeyCopyExternalRepresentation(publicKey, null)) as? NSData
                        check(manager.createFileAtPath(path, requireNotNull(publicData), null))
                    } finally { CFRelease(publicKey) }
                } finally { CFRelease(privateKey) }
            } finally { CFRelease(control) }
        } else {
            // Attribute lookup does not retrieve key material or request authentication.
            val status = withDictionary(query() + mapOf(cfObject(kSecReturnAttributes) to true,
                cfObject(kSecUseAuthenticationUI) to cfObject(kSecUseAuthenticationUIFail))) { attributes ->
                memScoped { val result = alloc<CFTypeRefVar>(); result.value = null
                    SecItemCopyMatching(attributes, result.ptr).also { result.value?.let { CFRelease(it) } }
                }
            }
            if (status == errSecItemNotFound) throw VaultKeyUnavailableException()
            check(status == errSecSuccess || status == errSecInteractionNotAllowed)
        }
        return withData(requireNotNull(publicData).asBytes()) { data ->
            val publicKey = requireNotNull(withDictionary(mapOf(cfObject(kSecAttrKeyType) to cfObject(kSecAttrKeyTypeRSA),
                cfObject(kSecAttrKeyClass) to cfObject(kSecAttrKeyClassPublic))) { SecKeyCreateWithData(data, it, null) })
            try { withData(key) { input ->
                val encrypted = requireNotNull(SecKeyCreateEncryptedData(publicKey, kSecKeyAlgorithmRSAEncryptionOAEPSHA256, input, null))
                (CFBridgingRelease(encrypted) as NSData).asBytes()
            } } finally { CFRelease(publicKey) }
        }
    }

    fun unwrap(wrapped: ByteArray, context: LAContext, reason: String): ByteArray? = memScoped {
        require(wrapped.size == 256)
        val result = alloc<CFTypeRefVar>(); result.value = null
        val status = withDictionary(query() + mapOf(cfObject(kSecReturnRef) to true,
            cfObject(kSecUseAuthenticationContext) to context, cfObject(kSecUseOperationPrompt) to reason)) {
            SecItemCopyMatching(it, result.ptr)
        }
        if (status == errSecUserCanceled) return@memScoped null
        if (status == errSecItemNotFound) throw VaultKeyUnavailableException()
        check(status == errSecSuccess)
        val privateKey: SecKeyRef = requireNotNull(result.value).reinterpret()
        try { withData(wrapped) { input ->
            val error = alloc<CFErrorRefVar>(); error.value = null
            val decrypted = SecKeyCreateDecryptedData(privateKey, kSecKeyAlgorithmRSAEncryptionOAEPSHA256, input, error.ptr)
            try {
                if (decrypted == null) {
                    val code = error.value?.let { CFErrorGetCode(it) }
                    if (code == errSecUserCanceled.toLong() || code == -2L) null else error("Authentication failed")
                } else (CFBridgingRelease(decrypted) as NSData).asBytes()
            } finally { error.value?.let { CFRelease(it) } }
        } } finally { CFRelease(privateKey) }
    }
}
