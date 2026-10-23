# Local Passwords

## User flow

Add opens the form immediately. A new password is visible while the user enters it, with an eye control to hide it. Saving requires a device screen lock but no biometric prompt. The list shows names and usernames with a fixed masked password placeholder. Its eye authenticates for that record and reveals the password; tapping again hides it. Tapping a card authenticates and opens the editor. Backgrounding the app hides revealed passwords and clears uncommitted password/notes text.

Names, usernames, websites, and timestamps are deliberately browsable metadata in the local database. Passwords and private notes are stored only inside encrypted payloads. Home does not preview credential details. There is no cloud, recovery, export, or dedicated clipboard integration.

## Encryption and authentication

Each new record receives an independent random encryption key. Android encrypts the payload with AES-256-GCM and wraps its 32-byte key using RSA-2048 OAEP with SHA-256 and MGF1-SHA1. The RSA public key permits saving without authentication. Its private key is non-exportable in Android Keystore and requires OS authentication to decrypt the selected record key. Android 11+ uses a cryptographically bound biometric/device-credential prompt. Android 7–10 uses device credential confirmation and a 30-second Keystore authentication window.

iOS uses separate 32-byte AES-256-CBC and HMAC-SHA256 keys, encrypt-then-MAC, wrapped together with RSA-2048 OAEP-SHA256. The private RSA key is a Keychain item with userPresence and WhenPasscodeSetThisDeviceOnly access. The public key is stored in the app directory and is not secret. This is a Keychain-protected software RSA key, not a Secure Enclave key.

The software RSA key deliberately does not use `privateKeyUsage`: [Apple documents that flag for Secure Enclave keys and states that generation outside Secure Enclave fails](https://developer.apple.com/documentation/security/secaccesscontrolcreateflags/privatekeyusage). The `userPresence` ACL gates retrieval of the private key through `SecItemCopyMatching`, using a fresh `LAContext`; public-key wrapping needs no authentication. [Apple's Keychain authentication guidance](https://developer.apple.com/documentation/localauthentication/accessing-keychain-items-with-face-id-or-touch-id) applies to passwords and private keys. The private-key reference is released after unwrap. This is OS-enforced access control on a software key, not hardware-isolated RSA or a guarantee against a compromised process after authorization. The old configuration's failure was identified from the documented API contract, not reproduced on a physical iPhone.

The versioned record ID authenticates each encrypted payload. The decrypted payload must contain the same ID. A successful authentication authorizes only that record in memory. Hide/background clears the temporary key and displayed secret. Mutable buffers are wiped where owned; immutable strings and OS/runtime memory do not guarantee erasure.

## Encryption format contracts

`PasswordEncryptionFormat` defines explicit integer IDs (not enum ordinals) matching Room's stored `format` field. Named constants avoid converters and permit explicit rejection of unknown IDs at the platform crypto boundary.

| ID | Constant | Envelope |
| --- | --- | --- |
| 1 | `ANDROID_AES_GCM_RSA_OAEP` | AES-256-GCM; 12-byte nonce; ciphertext followed by 16-byte tag; 32-byte record key wrapped with RSA-2048 OAEP SHA-256 / MGF1-SHA1, empty label. |
| 2 | `IOS_AES_CBC_HMAC_RSA_OAEP` | AES-256-CBC/PKCS7; 16-byte IV; ciphertext followed by 32-byte HMAC-SHA256 over AAD + IV + ciphertext; independent 32-byte AES/HMAC keys wrapped together with RSA-2048 OAEP-SHA256. |

Both require a 256-byte wrapped key. The platform rejects other IDs, missing/malformed wrapped keys, incorrect nonce/IV lengths and truncated tags; iOS also checks CBC block alignment. Authentication verifies actual ciphertext integrity. Checks run before unwrapping and again before payload decryption. There is no format conversion or whole-vault fallback.

The IDs are separate from both Room schema 1 and AAD `lilo-password-v1:<recordId>` (UTF-8), which remains unchanged. The AAD binds the record identity, not the numeric envelope format. Future incompatible contracts must receive new IDs and deliberate migration rules.

## Persistence and compatibility

The unreleased password feature starts with Room schema 1, containing credentials, browsable metadata, wrapped per-record keys, and services. Defaults are seeded once on database creation. There are no database migrations or destructive fallbacks. Earlier development databases require an explicit fresh setup; the app does not clear them automatically. The former development formats 3/4 have been renumbered to 1/2; former whole-vault 1/2 records (without per-record wrapped keys) are unsupported. No migration is provided. Test installations must start fresh, including password files and the app's RSA Keychain item on iOS, so an old ACL is not reused. Reinstalling iOS invokes the existing install-marker cleanup for the current RSA item; obsolete development-only vault items are no longer queried or managed.

Android data uses noBackupFilesDir. iOS excludes the directory from backup, uses complete file protection, and clears this app's leftover Keychain items on a fresh installation. Losing a required private key or removing device security may make existing records unreadable; the app does not silently replace those keys. [Android key authorization](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec) and [Apple accessibility policy](https://developer.apple.com/documentation/security/ksecattraccessiblewhenpasscodesetthisdeviceonly) govern these restrictions.

Password screens allow screenshots and screen capture. Backgrounding still wipes temporary keys and revealed secrets; an authenticated editor returns to the list instead of displaying a screen-level unlock gate. No raw passwords are intentionally logged; Android authentication diagnostics contain only exception class names or OS error codes.

## Verification

Tests cover the new IDs, unsupported IDs (including former 3/4), missing wrapped keys from whole-vault records, malformed envelopes, public-key wrapping, independent record keys, authenticated encryption/tampering, record scope, metadata-only browsing, adding without unlocking, reveal/hide, authentication cancellation, background hiding, and failed-save retry. Validation on 2026-10-23 after the format cleanup:

- `:composeApp:testDebugUnitTest`: 159 tests passed, no failures/errors/skips (includes common session, view-model and Android cipher/repository tests).
- `:composeApp:assembleDebug` and `:composeApp:assembleDebugAndroidTest`: succeeded. No Android device was connected, so instrumentation was compiled/packaged but not executed.
- `:composeApp:compileTestKotlinIosSimulatorArm64` and `:composeApp:iosSimulatorArm64Test`: compiled, linked and executed successfully; 142 tests passed, no failures/errors/skips, including the iOS CBC/HMAC tests. The formerly reported `UIViewLayoutRegion` linker error did not recur in this run.
- `:composeApp:compileKotlinIosArm64`: blocked at dependency resolution in `kspKotlinIosArm64`. Both offline and online attempts could not resolve the device-target klibs for AndroidX navigationevent 1.0.2, Compose runtime 1.10.5 and savedstate 1.3.3. No dependency versions were changed for this task.
- `git diff --check`: passed. Room's version and exported schema are unchanged.

The combined Gradle invocation used `--continue` so the device-target dependency failure did not prevent the Android and simulator checks above. Simulator tests exercise the cipher and shared state behavior; they do not exercise the protected Keychain RSA item or biometric UI. Android add/save, biometric reveal and hiding had been confirmed on the user's phone before this cleanup; those historical checks do not certify this revision. Physical-device cancellation and iOS runtime authentication remain unverified.

Physical-device acceptance for this cleanup must verify first-save key generation without a prompt, subsequent saves without authentication, biometric/passcode reveal, denied/cancelled Keychain access, hide/background/retry, and loss of keys after removal of device security. Android authentication code is unchanged, including the 30-second Keystore authorization window on Android 7–10; per-record memory scope is enforced by the shared session. Cipher tests and compilation cannot certify these OS authentication guarantees.

## Services

Service IDs and names are browsable organizational metadata. Service moves and renames never decrypt or rewrite credential ciphertext. The current database service assignment overrides the encrypted snapshot when an entry is opened. An indexed Room foreign key now prevents references to missing services. List/Home queries select public metadata without loading ciphertext or wrapped keys. A save requires an existing service in the same database transaction. Deleting an in-use service atomically reassigns records to an existing replacement.

Android add/save, biometric reveal, and hiding were confirmed on the connected phone before the service addition. iOS runtime authentication remains unverified.

Current database verification and remaining device checks are recorded in [the first-release code health audit](pre-release-health-audit.md).
