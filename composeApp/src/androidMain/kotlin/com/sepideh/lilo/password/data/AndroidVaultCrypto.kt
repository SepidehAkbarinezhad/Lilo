package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.DeviceLockRequiredException
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.VaultKeyUnavailableException

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class AndroidVaultCrypto(private val context: Context) : SessionVaultCrypto() {
    data class UnlockOperation(val cipher: Cipher, val input: ByteArray, val recordId: String)
    private val alias = "lilo.passwords.reader.rsa.v1"
    private val directory = passwordDirectory(context)
    private val publicFile = AtomicFile(File(directory, "reader-public.der"))
    private val writes = Mutex()
    private val keystore get() = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

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

    // Lilo's authentication strategy:
   // Keys created on Android 7–10 use time-based authentication (duration = 30).
   // Keys created on Android 11+ use per-operation authentication.(duration = 0)
   // Since OS upgrades preserve the existing key policy, check KeyInfo
   // instead of assuming the policy from the current Android version.
    fun usesTimedAuthentication(): Boolean {
        val key = keystore.getKey(alias, null) as? PrivateKey ?: throw VaultKeyUnavailableException()
        val info = KeyFactory.getInstance(key.algorithm, "AndroidKeyStore").getKeySpec(key, KeyInfo::class.java)
        check(info.isUserAuthenticationRequired)
        // Positive duration indicates time-based authentication.
        // -1 indicates per-operation authentication.
        return info.userAuthenticationValidityDurationSeconds > 0
    }

    fun prepareUnlock(request: PasswordUnlockRequest): UnlockOperation {
        require(request.id.isNotBlank())
        AndroidPasswordCipher.validateRecord(request.sealed)
        requireScreenLock()
        val store = keystore
        if (!store.containsAlias(alias)) throw VaultKeyUnavailableException()
        val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
        cipher.init(Cipher.DECRYPT_MODE, store.getKey(alias, null) as PrivateKey, AndroidPasswordCipher.oaep)
        return UnlockOperation(cipher, request.sealed.wrappedKey.copyOf(), request.id)
    }

    fun completeUnlock(operation: UnlockOperation) {
        try {
            val key = operation.cipher.doFinal(operation.input)
            try { require(key.size == 32); acceptAuthenticatedKey(key, operation.recordId) }
            finally { key.fill(0) }
        } finally { operation.input.fill(0) }
    }

    override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        AndroidPasswordCipher.decrypt(key, record, aad)
    }
}

fun passwordDirectory(context: Context): File = File(context.noBackupFilesDir, "passwords").also {
    check(it.isDirectory || it.mkdirs())
}
