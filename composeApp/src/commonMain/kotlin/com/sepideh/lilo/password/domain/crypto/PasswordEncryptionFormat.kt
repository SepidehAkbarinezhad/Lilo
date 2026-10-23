package com.sepideh.lilo.password.domain.crypto

/**
 * Persisted platform-specific envelope contracts for the first public release.
 * These IDs are independent of the payload/AAD version and Room schema version.
 * Unknown IDs must be rejected; never resolve them by enum ordinal or guess a cipher.
 */
object PasswordEncryptionFormat {
    /** AES-256-GCM, 12-byte nonce, 16-byte tag; RSA-2048 OAEP SHA-256 / MGF1-SHA1. */
    const val ANDROID_AES_GCM_RSA_OAEP = 1

    /** AES-256-CBC/PKCS7 + HMAC-SHA256 (encrypt-then-MAC); RSA-2048 OAEP-SHA256. */
    const val IOS_AES_CBC_HMAC_RSA_OAEP = 2
}
