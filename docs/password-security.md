# Local Passwords

## User flow

Add opens the form immediately. A new password is visible while the user enters it, with an eye control to hide it. Saving requires a device screen lock but no biometric prompt. The list shows names and usernames with a fixed masked password placeholder. Its eye authenticates for that record and reveals the password; tapping again hides it. Tapping a card authenticates and opens the editor. Backgrounding the app hides revealed passwords and clears uncommitted password/notes text.

Names, usernames, websites, and timestamps are deliberately browsable metadata in the local database. Passwords and private notes are stored only inside encrypted payloads. Home does not preview credential details. There is no cloud, recovery, export, or dedicated clipboard integration.

## Encryption and authentication

Each new record receives an independent random encryption key. Android encrypts the payload with AES-256-GCM and wraps its 32-byte key using RSA-2048 OAEP with SHA-256 and MGF1-SHA1. The RSA public key permits saving without authentication. Its private key is non-exportable in Android Keystore and requires OS authentication to decrypt the selected record key. Android 11+ uses a cryptographically bound biometric/device-credential prompt. Android 7–10 uses device credential confirmation and a 30-second Keystore authentication window.

iOS uses separate 32-byte AES-256-CBC and HMAC-SHA256 keys, encrypt-then-MAC, wrapped together with RSA-2048 OAEP-SHA256. The private RSA key is a Keychain item with user presence, private-key usage, and WhenPasscodeSetThisDeviceOnly access. The public key is stored in the app directory and is not secret. This is a Keychain-protected software RSA key, not a Secure Enclave key.

The versioned record ID authenticates each encrypted payload. The decrypted payload must contain the same ID. A successful authentication authorizes only that record in memory. Hide/background clears the temporary key and displayed secret. Mutable buffers are wiped where owned; immutable strings and OS/runtime memory do not guarantee erasure.

## Persistence and compatibility

The unreleased password feature starts with Room schema 1, containing credentials, browsable metadata, wrapped per-record keys, and services. Defaults are seeded once on database creation. There are no database migrations or destructive fallbacks. Earlier development databases require an explicit fresh setup; the app does not clear them automatically.

Android data uses noBackupFilesDir. iOS excludes the directory from backup, uses complete file protection, and clears this app's leftover Keychain items on a fresh installation. Losing a required private key or removing device security may make existing records unreadable; the app does not silently replace those keys. [Android key authorization](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec) and [Apple accessibility policy](https://developer.apple.com/documentation/security/ksecattraccessiblewhenpasscodesetthisdeviceonly) govern these restrictions.

Password screens allow screenshots and screen capture. Backgrounding still wipes temporary keys and revealed secrets; an authenticated editor returns to the list instead of displaying a screen-level unlock gate. No raw passwords are intentionally logged; Android authentication diagnostics contain only exception class names or OS error codes.

## Verification

Tests cover public-key wrapping, independent record keys, authenticated encryption/tampering, record scope, metadata-only browsing, adding without unlocking, reveal/hide, authentication cancellation, background hiding, failed-save retry. iOS test sources compile, but execution is blocked by the existing UIViewLayoutRegion linker/toolchain issue. Android add/save, biometric reveal, and hiding have been confirmed on the user’s phone. Device cancellation and iOS runtime authentication remain unverified.

## Services

Service IDs and names are browsable organizational metadata. Service moves and renames never decrypt or rewrite credential ciphertext. The current database service assignment overrides the encrypted snapshot when an entry is opened. A save requires an existing service in the same database transaction. Deleting an in-use service atomically reassigns records to an existing replacement. 

Android add/save, biometric reveal, and hiding were confirmed on the connected phone before the service addition. iOS runtime authentication remains unverified.
