# Build a signed Android release APK

The package remains `com.sepideh.lilo.kmp.app`, namespace `com.sepideh.lilo`, version code `1`, version name `1.0`. Code `1` is for the first publication of this new package; increment it for every subsequent store update. No signing key has been created by this change.

## Build separation

| Setting | Debug | Release |
| --- | --- | --- |
| Signing | Standard Android debug key | External key and four required environment variables |
| Debuggable | Yes | No |
| R8 / resource shrinking | Off | On, using optimized Android defaults and focused project rules |
| Compose inspection tooling | Included | Excluded (`debugImplementation`) |
| Koin diagnostics | Enabled | Disabled |
| Analytics / Crashlytics automatic collection | Disabled by Debug manifest | Enabled by SDK defaults |
| FCM | Enabled; no token/payload logging | Enabled; no token/payload logging |

Both variants use the existing Firebase project `lilo-kmp-6e652` and the registered package above. No new registration is needed. The Firebase client configuration contains public app identifiers/API configuration, not a signing private key or server credential. Do not add service-account files or signing passwords to it.

Debug and Release intentionally retain the same application ID: they cannot coexist or replace one another when signed with different keys. Do not uninstall a data-bearing installation merely to test signing; use a separate phone/emulator or preserve needed data first. There is no automatic migration from the old Bazaar package.

## 1. Create the permanent release keystore yourself

If you already created a key for this new package, use it instead. Otherwise, run the following yourself in macOS Terminal. These commands are instructions only; the agent has not run them. Choose and retain the certificate identity when prompted. Passwords are entered interactively, not placed in command history.

```zsh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
mkdir -p "$HOME/.android-signing/lilo"
chmod 700 "$HOME/.android-signing/lilo"
if [[ -e "$HOME/.android-signing/lilo/lilo-release.p12" ]]; then
  echo "Keystore already exists; stop and verify it."
else
  "$JAVA_HOME/bin/keytool" -genkeypair -v \
    -keystore "$HOME/.android-signing/lilo/lilo-release.p12" \
    -storetype PKCS12 -alias lilo-release \
    -keyalg RSA -keysize 3072 -validity 10000
fi
```

After successful creation, restrict the file to your account:

```zsh
chmod 600 "$HOME/.android-signing/lilo/lilo-release.p12"
"$JAVA_HOME/bin/keytool" -list -v \
  -keystore "$HOME/.android-signing/lilo/lilo-release.p12" -alias lilo-release
```

Keep an encrypted backup of the keystore separately from the Mac and retain the password in your password manager. Preserve the certificate SHA-256 fingerprint for future comparisons. The same key is needed for updates to this package; do not regenerate it for later versions. PKCS12 created this way uses the same password for the store and private key.

## 2. Supply signing credentials to this terminal session

From the repository root in **zsh**, enter the two passwords at the hidden prompts (use the same password twice for the PKCS12 above). Do not paste passwords into commands, Gradle properties, the chat, or a tracked file.

```zsh
cd /Users/sepideh/AndroidStudioProjects/Lilo
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export LILO_RELEASE_STORE_FILE="$HOME/.android-signing/lilo/lilo-release.p12"
export LILO_RELEASE_KEY_ALIAS="lilo-release"
read -rs 'LILO_RELEASE_STORE_PASSWORD?Keystore password: '
printf '\n'
read -rs 'LILO_RELEASE_KEY_PASSWORD?Private key password: '
printf '\n'
export LILO_RELEASE_STORE_PASSWORD LILO_RELEASE_KEY_PASSWORD
```

For an existing keystore, set its actual absolute path and alias instead. The path must resolve outside the repository. The build never falls back to the debug key. Missing variables stop `validateReleaseSigningCredentials` with an actionable error; an incorrect password/alias or unreadable key is rejected by Android signing tools. Git ignores common keystore and local signing-properties filenames as a second safeguard.

## 3. Validate and build

Validation on 2026-11-05: Debug APK, Release compilation/R8, Crashlytics mapping upload, variant manifests, and the missing-credentials guard were checked successfully. After restoring VPN access to Google Maven and refreshing dependency resolution, `:composeApp:lintRelease` passed online with 0 errors and 62 warnings. The only blocking code issue was a missing Persian `app_name` translation, now added. Remaining warnings include target SDK age, Android 12+ backup/transfer rules, missing FCM token-refresh observation, portrait orientation, and language splitting for future App Bundles. Lint remains enabled and dependency versions are unchanged; a full signed APK and device smoke test remain pending.

```zsh
./gradlew --no-daemon --no-configuration-cache :composeApp:validateReleaseSigningCredentials
./gradlew --no-daemon --no-configuration-cache :composeApp:assembleRelease
unset LILO_RELEASE_STORE_PASSWORD LILO_RELEASE_KEY_PASSWORD
```

Also unset the passwords if a command fails. Avoid Gradle build scans, verbose environment dumps, and configuration-cache use for signing builds. No password is stored in source or packaged into the APK by this configuration.

Successful output: `composeApp/build/outputs/apk/release/Lilo-release.apk`. R8 mapping: `composeApp/build/outputs/mapping/release/mapping.txt`. Retain the mapping with the exact APK/version. Only trust a freshly successful build; an old APK may remain after a failed build.

The Crashlytics Gradle plugin remains enabled, with mapping upload enabled by default for obfuscated Release builds. Internet access to Firebase is needed for mapping upload. Do not disable upload for the APK you publish, or readable crash reports may be unavailable. Firebase still needs a real-device crash/relaunch test to establish end-to-end reporting.

## 4. Verify before distribution

Using the installed SDK build tools:

```zsh
"$HOME/Library/Android/sdk/build-tools/36.0.0/apksigner" verify --verbose --print-certs \
  composeApp/build/outputs/apk/release/Lilo-release.apk
"$HOME/Library/Android/sdk/build-tools/36.0.0/aapt" dump badging \
  composeApp/build/outputs/apk/release/Lilo-release.apk
```

The signature must verify, its SHA-256 certificate fingerprint must match your keystore, and the package/version must be `com.sepideh.lilo.kmp.app`, `1`, `1.0`. There must be no `application-debuggable` flag. A debug certificate is not suitable for this release.

Install the signed APK on a test device and smoke-test task/note storage, reminders, password-vault unlock, navigation, and foreground/background FCM. A successful R8 build alone does not prove runtime behavior of the optimized app. Store acceptance requirements (including target SDK) must be checked separately; this change keeps the existing target SDK 34 and compile SDK 36.

References: [Android signing](https://developer.android.com/studio/publish/app-signing), [Crashlytics mapping files](https://firebase.google.com/docs/crashlytics/android/get-deobfuscated-reports), [FCM/Analytics testing in this repository](android-firebase-release-setup.md).
