# Task reminders

One task owns one optional reminder. `reminderAt` is the initial occurrence in epoch
milliseconds. `repeatRule` stores the stable codes NONE, DAILY or WEEKLY.
`reminderTimeZoneId` captures the device timezone when the user creates the reminder;
travelling does not silently change the saved timezone. Daily/weekly recurrence uses
calendar dates in that zone, not fixed 24-hour durations. No end date is needed.

The combined editor uses the shared flat form header and compact feature-coloured
Confirm action. Date, hour/minute and repetition are edited as one draft. Confirm
copies that draft into the task form; Back discards it. Saving the task persists and
schedules it. Clearing its reminder, completing or deleting the task cancels it.
Reopening a completed task restores its next future repeating occurrence. A past
one-time reminder is not replayed. Missed repeating occurrences are skipped.

Android retains the Persian calendar in RTL, and Gregorian in LTR. iOS retains the
existing Gregorian fallback. Hour/minute inputs accept Persian and Latin digits.
Weekly repetition uses the selected date's weekday. The initial date/time must be
future, except when retaining the unchanged anchor of an existing repeat.

## Scheduling

Android uses a single exact alarm for the next occurrence and re-arms after delivery.
The receiver reads current persistence under the same mutation lock before notifying,
so removed/completed/changed schedules cannot deliver a stale occurrence. Reboot,
clock changes, permission grant and foreground entry restore future schedules. Exact
alarm permission is checked on every Android manufacturer, including Xiaomi.

On iOS, native calendar repeats are used when their next firing matches the first
allowed occurrence. Native repeats cannot specify an arbitrary future start date.
For that case, dated notifications are queued and replenished on foreground entry.
The queue has a budget of 60 requests minus other features' pending requests; each
active task gets its first occurrence before remaining capacity is shared in date
order. If the app is never reopened, a deferred-start repeat can exhaust this queue.
This is a platform limitation of this implementation, not unlimited background
rescheduling. Normal immediately-applicable native repeats do not have this limit.

The common recurrence calculator shifts nonexistent DST times forward and uses the
earlier occurrence of an ambiguous time once. iOS native repeats follow the platform
calendar's DST matching behaviour. Device tests must cover both platforms.

## Task mutation synchronization (H4)

The singleton mutation lock orders task read/modify/write operations with reminder
validation, re-arming, notification delivery and cancellation. Room protects individual
transactions, but cannot make these database and operating-system operations atomic.
Removing the lock would allow a stale save to undo completion, or a receiver/restoration
snapshot to re-arm an alarm after a concurrent cancellation. A per-task lock would not
cover whole-list reconciliation or notification preference changes.

Image imports run before acquiring this lock. New imports have unique file names and
remain owned by the save until its database commit succeeds. The task is re-read under
the lock afterward: deletion rejects the save, completion is preserved, and attachments
removed by another save are not reattached from a stale form. Cancellation while waiting
for the lock or a failed commit cleans up only the staged imports. After a successful
commit, cancellation cannot remove its newly owned attachments.

Cleanup of obsolete images runs after releasing the lock, including on task deletion
and scheduling failure. Android alarm, completion and restoration receivers therefore
do not wait for image-provider copies or file deletion. Their existing `goAsync()` /
`finish()` handling is unchanged. This removes the image-I/O dependency; it does not
provide a hard deadline guarantee for database or OS service calls. No timeout that
could silently abandon a completion or reminder was introduced.

## First-release schema

Room schema remains version 1, with no migration and no destructive fallback, as
requested for the unreleased app. An existing development database has the old schema:
clear the test installation's app data or reinstall before testing. This loses local
test data. Released versions will require a real migration.

## Validation

The 12 common domain tests passed in a standalone JVM harness with Kotlin 2.3.10
and kotlinx-datetime 0.7.1. The Android build could not run because the workspace
has no Android SDK; iOS and rendered UI remain unverified. XML parsing and task
dependency boundary checks passed.

Common tests cover once-only delivery, future start dates, missed days, weekly
anchors, DST changes and task sorting. Run `:composeApp:testDebugUnitTest` and
`:composeApp:compileDebugKotlinAndroid` with an Android SDK installed. Build iOS on
macOS. Also check permission denial/grant, reboot, edit/clear/complete/delete before
an alarm, Back versus Confirm, Persian input, and iOS future-start queue replenishment.

## Permission and time-control follow-up

Permission explanations now share the minimal surface, typography and feature colour
used by the confirmation dialogs. Save checks notifications first, then exact alarms.
Only the missing permission is shown; returning from Settings rechecks and continues
the pending save. Android opens notification settings or alarms-and-reminders access
directly. iOS requests authorization initially, then uses its app settings fallback.
Cancel preserves the draft. Save without reminder clears the timestamp, timezone and
repeat rule before persisting and cancelling the existing task notification.

Hour/minute selection uses reusable bounded snapping wheels. Time and Repeat use
SectionTitle. The calendar is given finite height before enabling form scrolling, so
its internal lazy layout is never measured with unbounded vertical constraints.
The existing Persian picker implementations and dependencies are unchanged.

Task reminders use the approved original Lilo three-note chime, `lilo_reminder.wav`
(2.8 seconds, mono 44.1 kHz, 16-bit linear PCM). The Android raw resource and iOS
main-bundle resource contain the same unmodified audio approved on 2026-11-06.
There is one bundled sound and no in-app sound picker or additional setting.

New Android `task_reminders` channels use this sound with notification audio usage
and two 160 ms vibrations separated by 100 ms. Android 7 uses the same sound and
pattern directly on the notification; `VIBRATE` is a manifest permission without a
runtime prompt. The sound URI uses the resource name rather than a numeric resource
ID, so channel references survive resource-ID changes between builds. Keep the raw
resource name stable. Existing channel settings are respected; a silent/low-importance
channel logs a warning. A channel setting cannot be reset by calling
`createNotificationChannel` again, so older development installations may retain
their previous sound. First-release fresh installs get the Lilo sound by default.

iOS schedules `UNNotificationSound.soundNamed("lilo_reminder.wav")`; the WAV is
included in the app target's Copy Bundle Resources phase. iOS controls notification
haptics. Both platforms play through the OS notification system and respect user
sound/volume and quiet-mode settings; no looping player or alarm-volume override is
used. Cloud/FCM notifications retain their existing behavior.

Platform references: [Android notification channels](https://developer.android.com/develop/ui/compose/notifications/channels)
and [Apple notification sounds](https://developer.apple.com/documentation/usernotifications/unnotificationsound).

Sound validation on 2026-11-06: all 54 targeted task/reminder-preference tests passed;
`:composeApp:assembleDebug` and `:composeApp:compileKotlinIosSimulatorArm64` succeeded.
Both WAV copies and the packaged APK audio match the approved preview byte-for-byte.
Xcode project syntax and the app target's sound-resource membership were checked.
Physical-device sound/vibration playback and a full iOS app build remain unverified.
Device acceptance should cover fresh-channel delivery in the background, Android 7
and 8+ notification paths, quiet mode, and preserving existing user channel choices.

Validation for this follow-up: dependency boundary checks, resource XML parsing and
git whitespace checks passed. Compilation was blocked at Gradle download in the
current workspace; Android/iOS device execution remains unverified. Check each
permission separately, return without granting, grant both, cancel the dialog, save
without reminder on an existing task, and wheel values 00/23 and 00/59.

### Two-action dialog refinement

New reminders initialize to the current device time (existing reminders keep their
saved time); confirmation still requires a future time for a new reminder.
Permission dialogs now use the same shared two-action content as deletion dialogs:
Continue opens the permission flow; explicit Cancel opens a second confirmation.
Only confirming Save without reminder clears and saves. Keep editing, Back and
outside dismissal preserve the task draft and close the dialogs.
