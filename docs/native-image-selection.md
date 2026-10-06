# Native image selection

Image selection uses existing platform APIs and dependencies; FileKit is removed.

- `core/domain/images`: `ImageSource` is an opaque picker reference; `ImageStore` imports sources into private storage and returns relative names. The shared selection limit and filename validation live here.
- `core/presentation/components/picker`: common `rememberImagePicker` declares callbacks and launch behavior; native implementations live in the same package in `androidMain` and `iosMain`. Cancellation leaves the current selection unchanged.
- `core/data/images`: shared batch import rolls back partial copies on failure or cancellation. Platform stores interpret native sources and copy files. Core platform Koin modules register the implementations.
- Tasks owns its form actions, draft selection, attachment-name persistence, and task save/delete behavior. Priority controls and task cards remain feature-specific. Shared form rows, headers, date/time controls and group selection remain in core.

## Android

`ActivityResultContracts.PickMultipleVisualMedia` launches the system Photo Picker, restricted to images. AndroidX Activity already exists in the project and supplies the older-device fallback to `ACTION_OPEN_DOCUMENT`. Results are content URI references; only the native `ImageStore` interprets them with `ContentResolver`. Copies run on the IO dispatcher. The limit is enforced after fallback results as well as in the picker.

## iOS

`PHPickerViewController` selects multiple images without requesting broad Photos-library access. The screen remembers a session that strongly retains UIKit's weak delegate and owns provider loading jobs. `NSItemProvider.loadFileRepresentationForTypeIdentifier` supplies short-lived URLs; the native data adapter copies each file before its completion callback returns. Temporary copies are removed on disposal, cancellation or selection-loading failure. UIKit callbacks reach common UI only as `ImageSource` values.

## Lifetime and persistence

Picker sources are draft references for the current form session, not persistent attachment IDs. Save copies them into the private `images` directory; task records store only portable relative names. Removal also recognizes the previous `task-images` directory so the refactor does not discard earlier attachments. The database format and migration are unchanged by this native-picker replacement.

The native iOS migration test is under `iosTest`; Android host unit tests cannot load the Android SQLite JNI driver. The common mapping and import-policy tests run on Android's host test target without additional dependencies.
