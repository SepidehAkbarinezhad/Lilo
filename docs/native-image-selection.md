# Native image selection

Image selection uses existing platform APIs and dependencies; FileKit is removed.

- `core/domain/images`: `ImageSource` is an opaque picker reference; `ImageStore` imports sources into private storage and returns relative names. The shared selection limit and filename validation live here.
- `core/presentation/components/picker`: common `rememberImagePicker` declares callbacks and launch behavior; native implementations live in the same package in `androidMain` and `iosMain`. Cancellation leaves the current selection unchanged.
- `core/data/images`: shared batch import rolls back partial copies on failure or cancellation. Platform stores interpret native sources and copy files. Core platform Koin modules register the implementations.
- Tasks owns its form actions, draft selection, attachment-name persistence, and task save/delete behavior. Priority controls and task cards remain feature-specific. Shared form rows, headers, date/time controls and group selection remain in core.

## Android

`ActivityResultContracts.OpenDocument` and `OpenMultipleDocuments` launch the Storage Access Framework (SAF) file picker on every supported Android version, with the accepted MIME types restricted to `image/*`. This avoids the Google Play services photo-picker component, which can open a network-error screen on affected devices. AndroidX Activity already exists in the project; no extra dependency or broad storage permission is needed. Locally stored images can be selected offline; remote document providers may still require a connection.

Results are content URI references; only the native `ImageStore` interprets them with `ContentResolver`. Copies run on the IO dispatcher. SAF does not enforce the app's selection limit, so Lilo takes at most the allowed number of returned images. Cancelling preserves the previous selection. URI grants are used for the current form session without taking persistent grants; Save imports images into private storage, as before. See [Android's SAF documentation](https://developer.android.com/training/data-storage/shared/documents-files).

## iOS

`PHPickerViewController` selects multiple images without requesting broad Photos-library access. The screen remembers a session that strongly retains UIKit's weak delegate and owns provider loading jobs. `NSItemProvider.loadFileRepresentationForTypeIdentifier` supplies short-lived URLs; the native data adapter copies each file before its completion callback returns. Temporary copies are removed on disposal, cancellation or selection-loading failure. UIKit callbacks reach common UI only as `ImageSource` values.

## Lifetime and persistence

Picker sources are draft references for the current form session, not persistent attachment IDs. Save copies them into the private `images` directory; task records store only portable relative names. Removal also recognizes the previous `task-images` directory so the refactor does not discard earlier attachments. The database format and migration are unchanged by this native-picker replacement.

The first-release Room baseline and current validation are documented in [the code health audit](pre-release-health-audit.md). Development-only migration tests were replaced by Android instrumented database integrity tests. Common mapping and import-policy tests run on Android's host test target.
