package com.sepideh.lilo.password.data

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import com.sepideh.lilo.password.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

class AndroidVaultCrypto(private val context: Context) : SessionVaultCrypto() {
    data class UnlockOperation(val cipher: Cipher, val input: ByteArray, val recordId: String)
    private val alias = "lilo.passwords.reader.rsa.v1"
    private val legacyAlias = "lilo.passwords.wrapper.v1"
    private val directory = passwordDirectory(context)
    private val publicFile = AtomicFile(File(directory, "reader-public.der"))
    private val random = SecureRandom()
    private val writes = Mutex()
    private val keystore get() = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    // Android Keystore OAEP uses SHA-1 for MGF1; explicitly match it on the public-key path.
    private val oaep = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)

    private fun requireScreenLock() {
        if (!context.getSystemService(KeyguardManager::class.java).isDeviceSecure) throw DeviceLockRequiredException()
    }

    override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword = withContext(Dispatchers.IO) {
        writes.withLock {
            requireScreenLock()
            val store = keystore
            if (!store.containsAlias(alias)) {
                if (publicFile.baseFile.exists() || File(publicFile.baseFile.path + ".bak").exists()) throw VaultKeyUnavailableException()
                val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(2048).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1)
                    .setUserAuthenticationRequired(true)
                if (Build.VERSION.SDK_INT >= 30) spec.setUserAuthenticationParameters(0,
                    KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL)
                else spec.setUserAuthenticationValidityDurationSeconds(30)
                KeyPairGenerator.getInstance("RSA", "AndroidKeyStore").apply { initialize(spec.build()) }.generateKeyPair()
            }
            val encoded = store.getCertificate(alias).publicKey.encoded
            if (!publicFile.baseFile.exists()) {
                val stream = publicFile.startWrite()
                try { stream.write(encoded); publicFile.finishWrite(stream) }
                catch (e: Exception) { publicFile.failWrite(stream); throw e }
            }
            val publicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(encoded))
            AndroidPasswordCipher.seal(publicKey, plaintext, recordId)
        }
    }

    fun prepareUnlock(request: PasswordUnlockRequest): UnlockOperation {
        requireScreenLock()
        val store = keystore
        if (request.sealed.format == 3) {
            require(request.sealed.wrappedKey.size == 256)
            if (!store.containsAlias(alias)) throw VaultKeyUnavailableException()
            val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
            cipher.init(Cipher.DECRYPT_MODE, store.getKey(alias, null) as PrivateKey, oaep)
            return UnlockOperation(cipher, request.sealed.wrappedKey.copyOf(), request.id)
        }
        // Read-only compatibility with records created by the earlier whole-vault implementation.
        require(request.sealed.format == 1)
        if (!store.containsAlias(legacyAlias)) throw VaultKeyUnavailableException()
        val wrapped = AtomicFile(File(directory, "vault-key.bin"))
        val bytes = wrapped.openRead().use { it.readBytes() }
        try {
            require(bytes.size == 60)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, store.getKey(legacyAlias, null) as SecretKey,
                GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
            cipher.updateAAD("lilo-vault-key-v1".encodeToByteArray())
            return UnlockOperation(cipher, bytes.copyOfRange(12, bytes.size), request.id)
        } finally { bytes.fill(0) }
    }

    fun completeUnlock(operation: UnlockOperation) {
        try {
            val key = operation.cipher.doFinal(operation.input)
            try { require(key.size == 32); acceptAuthenticatedKey(key, operation.recordId) }
            finally { key.fill(0) }
        } finally { operation.input.fill(0) }
    }

    override suspend fun encrypt(key: ByteArray, plaintext: ByteArray, aad: ByteArray) =
        AndroidPasswordCipher.encrypt(key, plaintext, aad)

    override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        require(record.format == 1 || record.format == 3)
        AndroidPasswordCipher.decrypt(key, record.copy(format = 1), aad)
    }
}

fun passwordDirectory(context: Context): File = File(context.noBackupFilesDir, "passwords").also {
    check(it.isDirectory || it.mkdirs())
}
