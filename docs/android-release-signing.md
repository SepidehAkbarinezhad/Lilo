# Build a signed Android release APK

The package remains `com.sepideh.lilo.kmp.app`, namespace `com.sepideh.lilo`, version code `1`, version name `1.0`. Code `1` is for the first publication of this new package; increment it for every subsequent store update. No signing key has been created by this change.

## Build separation

| Setting | Debug | Release |
| --- | --- | --- |
| Signing | Standard Android debug key | Existing external key and four Gradle user-home properties |
| Debuggable | Yes | No |
| R8 / resource shrinking | Off | On, using optimized Android defaults and focused project rules |
| Compose inspection tooling | Included | Excluded (`debugImplementation`) |
| Koin diagnostics | Enabled | Disabled |
| Analytics / Crashlytics automatic collection | Disabled by Debug manifest | Enabled by SDK defaults |
| FCM | Enabled; no token/payload logging | Enabled; no token/payload logging |

Both variants use the existing Firebase project `lilo-kmp-6e652` and the registered package above. No new registration is needed. The Firebase client configuration contains public app identifiers/API configuration, not a signing private key or server credential. Do not add service-account files or signing passwords to it.

Debug and Release intentionally retain the same application ID: they cannot coexist or replace one another when signed with different keys. Do not uninstall a data-bearing installation merely to test signing; use a separate phone/emulator or preserve needed data first. There is no automatic migration from the old Bazaar package.

## 1. Keep using the existing keystore

Use the existing release keystore and its existing alias and passwords. Do not generate or replace a key. The keystore must remain outside the repository; the build requires an absolute path and rejects paths that resolve inside the repository, including symlinks. Both JKS and PKCS12 keystores are supported by the Android signing tools.

Retain the keystore backup and its certificate SHA-256 fingerprint. The same signing identity is required for updates to this package. This configuration change does not modify the keystore.

## 2. Configure credentials once in Gradle user home

Gradle reads `/Users/sepideh/.gradle/gradle.properties` by default, including when Android Studio starts the build. If Android Studio uses a custom Gradle user home, use the `gradle.properties` file in that directory instead. Do not put credentials in this repository's `gradle.properties`, module build file, or `local.properties`.

Prepare the user-home file with owner-only permissions. These commands preserve its existing contents:

```zsh
mkdir -p "$HOME/.gradle"
touch "$HOME/.gradle/gradle.properties"
chmod 600 "$HOME/.gradle/gradle.properties"
```

Open that file in a local editor and add the following entries, replacing every example value with the existing keystore's actual details. Preserve unrelated settings and keep only one entry for each name. Enter passwords privately in the editor, not in shell commands or chat.

```properties
LILO_RELEASE_STORE_FILE=/absolute/path/to/your/existing-keystore.jks
LILO_RELEASE_STORE_PASSWORD=<existing store password>
LILO_RELEASE_KEY_ALIAS=<existing key alias>
LILO_RELEASE_KEY_PASSWORD=<existing key password>
```

Values are Java properties, not shell expressions: do not quote them, and do not use `~` or `$HOME` in the keystore path. Escape literal backslashes as `\\` and escape any leading spaces in a password. The key password must be the existing key's password; use the store password only if they are actually the same.

This file stores passwords as plain text outside Git, protected by its file permissions. Do not copy it into the project or share it. Signing reads only Gradle properties via `providers.gradleProperty()`. The former `LILO_RELEASE_*` environment variables are not read; configure the four Gradle properties for both Android Studio and terminal builds. Missing or empty values are rejected.

Missing values stop `validateReleaseSigningCredentials` with the property names only. A missing/unreadable keystore is rejected, and Android's signing tools reject invalid keystores, incorrect passwords, or aliases without a usable private key during the build. Release packaging never falls back to the debug key or intentionally produces an unsigned APK. Debug builds and Gradle sync do not require release credentials.

## 3. Build directly from Android Studio

1. Sync the project after editing the user-home properties.
2. In Android Studio's Gradle tool window, execute `:composeApp:assembleRelease --no-configuration-cache`. This explicitly builds Release regardless of the currently selected Debug variant. No terminal credential exports or new-keystore wizard are needed.
3. Wait for **BUILD SUCCESSFUL** before using the APK.

For an early check of required settings and keystore readability, run `:composeApp:validateReleaseSigningCredentials --no-configuration-cache` in the same window. Passwords and alias validity are checked when the APK is signed, not by this preliminary check.

The equivalent terminal command, using the same user-home credentials, is:

```zsh
./gradlew :composeApp:assembleRelease --no-configuration-cache
```

Keep configuration caching disabled for signing builds. Avoid Gradle's `properties` report, build scans, and verbose diagnostic dumps when credentials are configured: they can expose build properties. This configuration does not log credentials or add them to the APK.

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

References: [Gradle user-home properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties), [Android signing](https://developer.android.com/studio/publish/app-signing), [Crashlytics mapping files](https://firebase.google.com/docs/crashlytics/android/get-deobfuscated-reports), [FCM/Analytics testing in this repository](android-firebase-release-setup.md).
