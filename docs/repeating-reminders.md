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
