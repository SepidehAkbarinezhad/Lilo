# Local Passwords

## User flow

Add opens the form immediately. A new password is visible while the user enters it, with an eye control to hide it. Saving requires a device screen lock but no biometric prompt. The list shows names and usernames with a fixed masked password placeholder. Its eye authenticates for that record and reveals the password; tapping again hides it. Tapping a card authenticates and opens the editor. Backgrounding the app hides revealed passwords and clears uncommitted password/notes text.

Names, usernames, websites, and timestamps are deliberately browsable metadata in the local database. The primary encrypted payload contains only the exact UTF-8 `secret`, with no JSON, trimming, normalization, or duplicated metadata. `notes` is plain text in Room, like the other metadata; it has no encryption or cryptographic integrity protection. This does not change the independent Notes feature. The existing list/search projection still omits notes; the editor reads them from Room. Home does not preview credential details. There is no cloud, recovery, export, or dedicated clipboard integration.

## Encryption and authentication

Each save receives a fresh independent random record key. Android encrypts the payload with AES-256-GCM and wraps its 32-byte key using RSA-2048 OAEP with SHA-256 and MGF1-SHA1. The RSA public key permits saving without authentication. Its private key is non-exportable in Android Keystore and requires OS authentication to decrypt the selected record key. Android 11+ uses a cryptographically bound biometric/device-credential prompt. Android 7–10 uses device credential confirmation and a 30-second Keystore authentication window.

iOS uses separate 32-byte AES-256-CBC and HMAC-SHA256 keys, encrypt-then-MAC, wrapped together with RSA-2048 OAEP-SHA256. The private RSA key is a Keychain item with userPresence and WhenPasscodeSetThisDeviceOnly access. The public key is stored in the app directory and is not secret. This is a Keychain-protected software RSA key, not a Secure Enclave key.

The software RSA key deliberately does not use `privateKeyUsage`: [Apple documents that flag for Secure Enclave keys and states that generation outside Secure Enclave fails](https://developer.apple.com/documentation/security/secaccesscontrolcreateflags/privatekeyusage). The `userPresence` ACL gates retrieval of the private key through `SecItemCopyMatching`, using a fresh `LAContext`; public-key wrapping needs no authentication. [Apple's Keychain authentication guidance](https://developer.apple.com/documentation/localauthentication/accessing-keychain-items-with-face-id-or-touch-id) applies to passwords and private keys. The private-key reference is released after unwrap. This is OS-enforced access control on a software key, not hardware-isolated RSA or a guarantee against a compromised process after authorization. The old configuration's failure was identified from the documented API contract, not reproduced on a physical iPhone.

The versioned record ID authenticates the secret through AAD; the raw UTF-8 payload contains no embedded ID. UTF-8 encoding/decoding rejects invalid sequences rather than silently replacing characters. A successful authentication authorizes only that record in memory. Hide/background clears the temporary key and displayed secret. Mutable buffers are wiped where owned; immutable strings and OS/runtime memory do not guarantee erasure.

## Encryption format contracts

`PasswordEncryptionFormat` defines explicit integer IDs (not enum ordinals) matching Room's stored `format` field. Named constants avoid converters and permit explicit rejection of unknown IDs at the platform crypto boundary.

| ID | Constant | Envelope |
| --- | --- | --- |
| 1 | `ANDROID_AES_GCM_RSA_OAEP` | AES-256-GCM; 12-byte nonce; ciphertext followed by 16-byte tag; 32-byte record key wrapped with RSA-2048 OAEP SHA-256 / MGF1-SHA1, empty label. |
| 2 | `IOS_AES_CBC_HMAC_RSA_OAEP` | AES-256-CBC/PKCS7; 16-byte IV; ciphertext followed by 32-byte HMAC-SHA256 over AAD + IV + ciphertext; independent 32-byte AES/HMAC keys wrapped together with RSA-2048 OAEP-SHA256. |

Each record stores `nonce`/`ciphertext` for the secret, its `format`, and a 256-byte `wrappedKey`. Notes are stored in the plain-text `notes` column. The platform rejects other IDs, missing/malformed wrapped keys, incorrect nonce/IV lengths and truncated tags; iOS also checks CBC block alignment. Authentication verifies actual ciphertext integrity. Checks run before unwrapping and again before payload decryption. There is no format conversion or whole-vault fallback.

The IDs are separate from Room schema 1 and the AAD versions. AAD remains `lilo-password-v1:<recordId>` (UTF-8). It binds record identity, not the numeric envelope format or metadata. Future incompatible contracts must receive new IDs and deliberate migration rules.

## Metadata integrity

Public metadata is intentionally not authenticated by the ciphertext. Previously its encrypted duplicate supplied metadata after unlock, although list metadata was not compared with it; that protected duplicate no longer exists. Direct modification of title, username, website, notes, or timestamps in the database can now affect both listing and editing without a crypto error. This is an explicit integrity trade-off, not increased confidentiality. Record-bound AAD still rejects moving the secret ciphertext to another record ID. The design does not add rollback protection or protection against a compromised authorized app process.

Metadata-dependent AAD is not introduced: service deletion/reassignment and rename are valid paths that must work without decryption. `saveWithService` atomically saves metadata (including notes) and the secret ciphertext, and service reassignment atomically updates only Room relationships. Search/list projections load neither ciphertext nor the wrapped key. No independent metadata copy needs synchronization.

## Persistence and compatibility

The unreleased password feature starts with Room schema 1, containing credentials, browsable metadata, wrapped per-record keys, and services. Its first-release schema now includes a plain-text `notes` column. The old development schema/full-model JSON payload is incompatible; start with a fresh password database. No JSON fallback, automatic deletion, or migration is added. Envelope IDs remain 1/2 because the algorithms and envelope contracts are unchanged; the exported schema is updated before public release. Defaults are seeded once on database creation. There are no database migrations or destructive fallbacks. Earlier development databases require an explicit fresh setup; the app does not clear them automatically. The former development formats 3/4 have been renumbered to 1/2; former whole-vault 1/2 records (without per-record wrapped keys) are unsupported. No migration is provided. Installations predating the earlier format/key-ACL cleanup must also refresh password files and the app's RSA Keychain item on iOS so an old ACL is not reused. This secret-payload refactor itself only needs a fresh password database; it does not require deleting independent Notes data or replacing the current RSA key. Reinstalling iOS invokes the existing install-marker cleanup for the current RSA item; obsolete development-only vault items are no longer queried or managed.

Android data uses noBackupFilesDir. iOS excludes the directory from backup, uses complete file protection, and clears this app's leftover Keychain items on a fresh installation. Losing a required private key or removing device security may make existing records unreadable; the app does not silently replace those keys. [Android key authorization](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec) and [Apple accessibility policy](https://developer.apple.com/documentation/security/ksecattraccessiblewhenpasscodesetthisdeviceonly) govern these restrictions.

Password screens allow screenshots and screen capture. Backgrounding still wipes temporary keys and revealed secrets; an authenticated editor returns to the list instead of displaying a screen-level unlock gate. No raw passwords are intentionally logged; Android authentication diagnostics contain only exception class names or OS error codes.

## Verification

The repository tests use actual Android AES-GCM and iOS CBC/HMAC via test-only adapters. They cover exact secret UTF-8/whitespace, plain-text notes storage and edits, Room metadata reconstruction, service changes, invalid ciphertext/AAD/formats, failed saves, and session locking. Notes-specific encryption tests have been removed. The existing Room instrumentation fixture now uses plain-text notes.

Targeted validation on 2026-10-26:

- `:composeApp:testDebugUnitTest` filtered to `PasswordRepositoryTest`, `PasswordEncryptionTest`, and `SessionVaultCryptoTest`: 16 tests passed, no failures/errors/skips.
- `:composeApp:compileTestKotlinIosSimulatorArm64` and `:composeApp:compileDebugAndroidTestKotlinAndroid`: succeeded, including their production-code compilation prerequisites.
- Android and iOS generated the same first-release Room schema 1 with `notes` as TEXT; the exported schema was updated from that generated output.
- No full suite, framework build, simulator/emulator runtime test, or migration was run/added for this correction.

Simulator/emulator tests do not certify hardware-backed key protection or interactive OS authentication. Repository fixture keys are test-only; production still generates and wraps fresh keys. No dependency versions or biometric implementation were changed.

Physical-device acceptance for this refactor must verify first-save key generation without a prompt, subsequent saves without authentication, biometric/passcode reveal, denied/cancelled Keychain access, hide/background/retry, and loss of keys after removal of device security. Android authentication code is unchanged, including the 30-second Keystore authorization window on Android 7–10; per-record memory scope is enforced by the shared session. Cipher tests and compilation cannot certify these OS authentication guarantees.

## Services

Service IDs and names are browsable organizational metadata. Service moves and renames never decrypt or rewrite credential ciphertext. Room is the only source of public metadata and service assignment; the Repository combines those columns and current service names with the decrypted secret. No full-model snapshot is stored. The Domain `Password` model has no serialization annotations. An indexed Room foreign key now prevents references to missing services. List/Home queries select public metadata without loading ciphertext or wrapped keys. A save requires an existing service in the same database transaction. Deleting an in-use service atomically reassigns records to an existing replacement.

Android add/save, biometric reveal, and hiding were confirmed on the connected phone before the service addition. iOS runtime authentication remains unverified.

Current database verification and remaining device checks are recorded in [the first-release code health audit](pre-release-health-audit.md).
