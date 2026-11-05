# Android identity and Firebase setup

Android application ID: `com.sepideh.lilo.kmp.app`. Kotlin packages and the Android namespace remain `com.sepideh.lilo`; iOS is unchanged. Version code/name remain `1` / `1.0`. Release signing reads external environment variables; see [signed APK instructions](android-release-signing.md). No signing key is stored in this repository.

This is a separate Android app, not an update to the existing Bazaar installation. It gets separate local storage; existing app data does not migrate automatically. Keep the old installation if its data is needed.

## Required Firebase Console steps

1. Open the existing project **`lilo-kmp-6e652`** in [Firebase Console](https://console.firebase.google.com/project/lilo-kmp-6e652/settings/general).
2. In **Project settings → General → Your apps**, choose **Add app → Android**. Register package **`com.sepideh.lilo.kmp.app`** exactly. If already registered, select that app instead. Keep the existing Android and iOS registrations.
3. Download that registration's **google-services.json** and replace `composeApp/google-services.json`. Do not manually change the package field in the old JSON: the Firebase app ID and other settings must come from the real registration. The current file registers `com.sepideh.lilo.kmp.app`. Keep using the Console-generated configuration for this registration.
4. SHA certificate fingerprints are not required for the three services currently used (Analytics, Crashlytics, FCM). Do not create a release signing key as part of this setup. Check API-key Android application restrictions, if configured, permit the new package and the certificate used for testing.
5. Check **Project settings → Integrations → Google Analytics** is connected to the existing Analytics property. Open **Crashlytics**, select the new Android app, and complete any onboarding shown. No SDK/dependency changes are needed.
6. Sync Gradle, then run `./gradlew :composeApp:assembleDebug`. Install/run the debug variant from Android Studio on your phone. A release build remains a separate step after signing is arranged.

See [Firebase Android registration](https://firebase.google.com/docs/android/setup) and [Google Services configuration matching](https://firebase.google.com/docs/android/google-services-plugin-and-file).

## What the code implements

| Service | Implemented | Still requires verification |
| --- | --- | --- |
| Analytics | Android SDK via the Firebase BoM; automatic initialization/collection. No custom events or Compose destination tracking. | Automatic events appearing for the new app in Analytics DebugView. |
| Crashlytics | Android SDK and Gradle plugin for automatic fatal crash reporting and release mapping integration. No custom keys, nonfatal reporting, or permanent test-crash control. | A controlled test crash followed by relaunch and a report in the new app's Crashlytics dashboard. |
| FCM | Manifest messaging service, startup token retrieval, subscription to `all_users`, data-first title/body/image handling with OS permission checks, bounded HTTPS image loading and text fallback. Background notification payloads are displayed by the Firebase SDK. | Token retrieval, subscription, and foreground/background delivery on the actual phone with the new registration. |

Code inspection establishes integration, not successful backend delivery. No phone or Firebase Console delivery test has been performed for the new ID.

## Test FCM on a phone

1. Use a phone with compatible Google Play services and internet access to Firebase. Launch the newly installed `com.sepideh.lilo.kmp.app` at least once.
2. In Android **Settings → Apps → Lilo → Notifications**, enable notifications. Android 13+ requires notification permission; Lilo currently has no launch-time runtime permission prompt. Exact-alarm access is unrelated to FCM. If both Lilo installations exist, open App info from the new app to avoid changing the old one.
3. In Android Studio, set a breakpoint inside the successful token callback in `LiloApplication.onCreate` (the `subscribeToTopic()` call). Start the **debug** build with the debugger. Inspect/evaluate **`task.result`** and copy the registration token privately, then resume execution. Current logs intentionally do not print the token; no token logging was added. Use the new installation's token, not an old token or a Firebase app ID.
4. In the same Firebase project, open **Messaging**, create a **Firebase Notification** message, and enter a title and body. Choose **Send test message**, paste the device's FCM registration token, and select **Test**. This targets the device directly; do not send a campaign to `all_users` just to test one phone.
5. First press Home to put the app in the background (do not force-stop it), then send. Expect a system notification; tapping should open Lilo. Bring Lilo to the foreground and send again: expect one notification; temporary receipt/display and token logs have been removed. A heads-up banner also depends on channel settings and Do Not Disturb.
6. If delivery fails, confirm the package/token/project, notification permission and channel settings, Google Play services, network reachability and manufacturer background restrictions. Reinstalling or clearing app data can change the token; fetch it again. Background notification messages normally bypass `onMessageReceived`, so missing service logs in that state alone do not prove failed delivery.

## Payload behavior and image testing

- Data fields `title`, `body`, and `image_url` take precedence independently; missing/blank fields fall back to the notification payload. Absent text uses localized defaults.
- In the foreground, notification-only, data-only, and mixed messages reach the service. In the background, only data-only messages reach it; Firebase displays notification/mixed messages automatically. The app does not re-post data extras on notification taps.
- For consistent data precedence and custom `image_url` rendering in both states, send **data-only** messages from a trusted server using HTTP v1 or the Admin SDK. For time-sensitive visible notifications, use Android high priority. Normal-priority messages can be delayed in Doze; force-stopped apps must be reopened.
- For background notification/mixed messages, set title/body and image in the standard notification fields (Android image: `android.notification.image`). Firebase's renderer does not read custom `data.image_url`. Console tests exercise the notification path, not data-only delivery.
- The custom loader accepts HTTPS without embedded credentials, rejects redirects, caps downloads at 1 MiB, samples images to at most 1024 pixels per dimension, and waits at most two seconds for download/decode before falling back to text. Slow, invalid, oversized, non-image and failed responses are text-only. No WorkManager, disk cache or new dependency is used.
- Both display paths default to the existing `lilo_channel` and Lilo's monochrome icon. Custom notifications open Lilo on tap. The channel is initialized at application startup without changing existing user channel settings.

On the phone, send data-only messages containing title/body and a direct HTTPS image URL, once foregrounded and once backgrounded. Expand each notification to check the image. Repeat with an HTTP URL, broken HTTPS URL, oversized image and slow endpoint: each should yield one text-only notification. Send mixed payloads with different data/notification text to confirm foreground data precedence and background SDK rendering. Disable notifications and confirm nothing is shown. Re-delivery with the same FCM message ID updates the existing notification rather than adding another.

Remaining limitations: there is no server-side per-user token registration (unnecessary for a Console test). The `all_users` topic remains shared within the Firebase project, including older clients. The in-app reminder toggle controls local reminders; FCM observes OS notification permission rather than that preference.

See [FCM client setup and device testing](https://firebase.google.com/docs/cloud-messaging/android/get-started) and [foreground/background behavior](https://firebase.google.com/docs/cloud-messaging/android/receive-messages).

## Verify the other services

Automatic Analytics and Crashlytics collection is disabled by `src/debug/AndroidManifest.xml` for Debug; FCM remains enabled. Release retains automatic collection. For an intentional disposable Debug test, temporarily change the corresponding collection metadata to `true`, rebuild/reinstall, and restore it afterward. Do not commit that temporary change.

For Analytics, after enabling collection for the test, enable debug reporting with `adb shell setprop debug.firebase.analytics.app com.sepideh.lilo.kmp.app`, reopen/use the app, and inspect Firebase **Analytics → DebugView**. Disable it afterward with `adb shell setprop debug.firebase.analytics.app .none.`. Automatic events do not imply feature-specific tracking. See [Analytics DebugView](https://firebase.google.com/docs/analytics/debugview).

For Crashlytics, follow the [official controlled test-crash procedure](https://firebase.google.com/docs/crashlytics/android/test-implementation) on a disposable debug installation with collection temporarily enabled, relaunch, and confirm the report for the new registration. Remove any temporary crash trigger before release; none is included in this change.
