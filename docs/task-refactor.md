# Task refactor

Base: `develop`. Existing Notes source files are deliberately unchanged.

## Boundaries

- Task screens and ViewModels depend on domain models, repository contracts, and use cases. No Task presentation import may reference data packages.
- `TaskMutations` serializes task writes and owns reminder side effects. Completion reads the current row instead of upserting a stale UI snapshot.
- `Reminder` and `ReminderPermissions` belong to domain. Platform implementations and Room stay in data, assembled by Koin.
- UI validation and group-to-UI mapping live in presentation. Old import adapters remain for the legacy Notes implementation.
- Shared group selection and date/time pickers accept values and callbacks. They do not fetch data or schedule notifications.
- `TaskPriority` carries business meaning. UI labels/colors stay in presentation. Stable IDs are looked up explicitly, never used as indexes in the refactored Task UI.

The existing `Task.priority: Int` signature is retained because untouched Notes code consumes it. Full replacement with a typed field can follow removal of that dependency. Existing integer database values remain 0=High, 1=Medium, 2=Low; unknown values map to Medium.

## Behavior

- A single reactive pipeline combines task data, group selection, search, status, and priority filters. Empty status/priority selections mean no restriction. Filter reset clears the draft; Apply commits it; dismiss discards it.
- Creation/modification/completion timestamps are maintained. Creation time survives editing; reopening clears completion time. Existing zero creation times are initialized on the next write, not backdated.
- Description is optional. Save is in the header. Group and reminder values are visible in compact rows.
- Group selection is a bottom sheet with header confirmation. The footer has an add icon and text-only management action on opposite sides. Add replaces the footer with a name field, confirm, and cancel. Creating a group selects it in the draft without closing the sheet.
- Group deletion keeps tasks and clears their group reference. The current management dialog supports deletion; richer rename/reorder management and its Settings entry remain follow-up work.
- Editing/removing a reminder cancels its previous registration. Completion cancels it; reopening schedules a future reminder. Date/time cancellation preserves the committed reminder.
- Save records the persisted ID even if reminder scheduling throws, avoiding a duplicate task when retrying.
- List rows open on tap, expose a completion control, and offer swipe-to-delete with confirmation and an accessibility delete action. Settings stays on Home.

## Storage and scope

No entity columns or database versions change in this PR. The repository already has a version-1 entity with new timestamp columns but no committed schema history. Establish the installed schema before a later storage migration. This PR does not solve any pre-existing version-1 identity mismatch.

Photo attachment storage/picking, group foreign keys, a single-instant reminder schema, and a full Notes rewrite are not implemented here. Date/time selection preserves existing storage semantics. Persian Android uses its existing picker; Persian iOS falls back to the shared Gregorian picker instead of opening nothing. Full Persian calendar parity needs a separate implementation.

The existing iOS scheduler reports asynchronous notification errors through its callback rather than its interface; the use case can currently detect only synchronous failures.

## Validation

- `python scripts/check_task_boundaries.py`
- `git diff --check`
- New English/Persian resource files parsed as XML.
- `commonTest/TaskRulesTest.kt` covers status combinations/reset semantics, compound filtering, unknown priority IDs, sorting, and timestamp transitions.
- Local Gradle build/test is blocked before configuration: the environment cannot download Gradle 8.13 from services.gradle.org. No successful compile or device run is claimed.

Before merging, run `./gradlew :composeApp:testDebugUnitTest :composeApp:assembleDebug` in an Android SDK environment, and build the iOS simulator target on macOS. Verify add/edit, repeated filters, group create/cancel/confirm, reminder edit/cancel/reopen/delete, and English/Persian light/dark layouts on devices.

## UI refinement against new_task_form.png

The follow-up keeps `BaseHeader` and introduces shared `BaseFormScreen` and `BaseListScreen` scaffolds. `SheetHeader` shares title/action styling between group selection and filters. These are presentation-only components with values/callbacks.

`AppText` retains the project typography abstraction and consistently inherits Material content color and start alignment in both overloads. TextType adds explicit screen-title, section-title, field-label, action, body-large, and caption roles based on Material typography. Screen/sheet headings and actions use semibold weights. Field labels have 8dp separation from the outline, and the previously unused textStyle parameter is now applied to input text.

The form uses Low/Medium/High chips with colored dots, a compact filled Save button, neutral outline icons, and right-aligned value badges. Group confirmation is filled; group rows have subtle dividers; Manage groups is outlined. Search animates into a rounded outlined input, focuses automatically, and puts Close at the opposite logical edge from the collapsed search action. RTL follows layout direction. Closing search clears its query through the existing ViewModel action.

Photos remain unimplemented and are not represented by a nonfunctional button. Reminder clearing remains an explicit small action beside the value badge. No pixel-perfect or runtime verification is claimed while Gradle is unavailable. Shared text/header changes also affect screens consuming those components, though Notes source files remain unchanged.

## Shared form components

Based on user commit b401313. FormSelectionRow and group selection/management UI
live in core/presentation/components and accept display values/callbacks only.
Category persistence remains in the category feature; expenses can supply their
own group data without depending on task state. TaskPrioritySelector stays in
task/presentation/detail/components. Components have standalone AppPreviews.

Filter and Add collapse search and dismiss the keyboard without clearing the
query. Reopening search restores the query for editing. Priority and selection
rows are disabled during task loading/saving. Notes sources are unchanged.

Validation: dependency boundary and whitespace checks pass. Gradle compilation
could not run because the wrapper distribution download is unreachable here.

## Preview, localization and delete follow-up

The group package follows the user's rename from selection to group. Group
previews now use direct Preview annotations and window-free content composables;
this avoids relying on custom multi-preview discovery and dialog window rendering.
Android Studio rendering still needs local verification. Added the missing
Persian tasks_list_title resource.

DeleteConfirmationDialog now lives physically in core/presentation/components,
accepts feature accent/title/message/callbacks, and has no decorative image. Both
task and group deletion use it. SwipeToRevealDelete reveals an explicit action
in the logical end direction (mirrored for RTL); dragging never requests deletion.
Verify partial swipe, reveal, reverse swipe, Delete, Cancel, Confirm and TalkBack
on a device. XML, whitespace and architecture checks pass; Gradle remains blocked
by the unavailable wrapper download in this environment.
