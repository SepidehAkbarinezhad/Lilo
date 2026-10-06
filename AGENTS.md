# Lilo — Project Context and Development Principles

## What Lilo is

Lilo (لیلو) is a personal organizer that brings everyday information into one calm, coherent app. The product should feel modern, warm, lightweight, and easy to use, with consistent interactions across its features.


The product includes these feature areas; this describes the product direction, not a claim that every feature is complete:

- **Tasks / activities:** titles, descriptions, completion, priorities, groups, reminders, and image attachments.
- **Notes:** grouped personal notes with support for images.
- **Expenses:** personal expense tracking with its own categories and relevant date selection.
- **Passwords:** organized credentials, requiring a dedicated security design before implementation.
- **Home:** an overview of the features with useful previews of recently added content and clear empty states.
- **Settings:** language, appearance, and relevant app preferences. Optional home quotes should be configurable and hideable.

Lilo initially stores user data locally. Cloud synchronization and backup are future product work; do not introduce them incidentally during another feature change.

## Technology and architecture

Lilo uses Kotlin Multiplatform and Compose Multiplatform for Android and iOS. The established technology direction includes:

- Kotlin, coroutines, and Flow.
- Compose Multiplatform with Material 3.
- Clean Architecture and MVI-style unidirectional state flow.
- Koin for dependency injection.
- Room Multiplatform for structured local persistence.
- DataStore for preferences.
- Platform implementations for notifications, permissions, scheduling, and other operating-system capabilities.

Use the repository's existing dependency versions and conventions. Prefer multiplatform-compatible libraries for shared functionality. Shared Compose APIs may use the `androidx` namespace; distinguish those from Android-only APIs.

Keep reusable business logic and UI in `commonMain`. Isolate platform-specific code in the appropriate source set behind clear interfaces or `expect`/`actual` declarations. Do not replace a multiplatform picker or component with an Android-only implementation in shared code.

### Layer responsibilities

- **Domain:** business models, rules, repository contracts, and use cases where orchestration is needed. It must not depend on presentation, persistence implementations, Compose, or platform UI.
- **Data:** database entities, DAOs, mappings, repository implementations, and platform-backed services implementing domain contracts.
- **Presentation:** UI state, user actions, effects, view models, and composables. Access application data through domain contracts rather than DAOs or concrete data repositories.

Business concepts such as priority and repetition belong in domain models. Labels, icons, colors, and localized formatting belong in presentation. Resolve persisted identifiers by their explicit IDs, never by their position in a list.

Use design patterns to solve concrete problems. Prefer clear composition, dependency injection, and small focused responsibilities. Avoid speculative abstractions, unnecessary layers, and generic frameworks created for a single simple use case.

## Organization and reusable components

Organize code by feature, with domain, data, and presentation responsibilities clearly separated. Put genuinely reusable UI in `core/presentation` and its component subpackages.

Extract meaningful composables into a feature's `components` package even when they are not shared. This keeps screens readable and allows focused previews and editing. Screens should primarily assemble components and connect state with callbacks.

Reuse common patterns such as:

- List and form headers.
- Search and filter controls.
- Form selection rows and outlined text fields.
- Group selection and management UI.
- Date, time, reminder, and image selection components.
- Confirmation and permission dialogs.

Shared components receive values, state, feature colors, and callbacks. They should not depend on a task-specific view model, action type, or repository.

Shared UI does not require shared data. Tasks and notes may use shared groups where appropriate; expenses may have separate categories. Keep the group picker reusable regardless of its data source. Do not move domain or persistence code into `core/presentation` merely because several features reuse the UI.

## State and interactions

Follow the existing MVI-style flow: user actions reach the view model, the view model updates observable state, and composables render that state. Use immutable state models and explicit state transitions.

Separate durable UI state from one-time effects such as navigation or messages. Avoid triggering persistence, scheduling, or navigation from recomposition. Keep temporary picker edits separate from committed form values until confirmation.

Use structured concurrency, preserve cancellation, and handle failures explicitly. Keep permission and platform-service decisions outside reusable UI components.

## Visual identity

Each feature has one recognizable accent color. Use the theme's semantic color tokens rather than scattering literal colors through components.

| Area | Light-theme accent |
| --- | --- |
| Brand | Deep Orange 500 — `#FF5722` |
| Tasks | Amber 500 — `#FFC107` |
| Notes | Green 500 — `#4CAF50` |
| Expenses | Blue 700 — `#1976D2` |
| Passwords | Purple 500 — `#9C27B0` |

Use theme-specific variants for dark mode. Feature accents identify actions and selected states; priority colors communicate priority independently. Group chips remain neutral unless a separate category-color feature is deliberately introduced.

### General design rules

- Aim for modern, restrained, polished UI with generous but purposeful spacing.
- Use a warm, near-white light-theme background and clean surfaces. Avoid unintended purple Material defaults.
- Keep headers visually integrated with the screen. Avoid unnecessary separators and heavy shadows.
- Use consistent rounded line icons with balanced sizes and stroke weights. Subtle circular backgrounds can give action icons presence without visual noise.
- Keep equivalent icons and labels consistent in color. Avoid unexpectedly black icons or text beside softer controls.
- Use clear typography hierarchy: titles and actions should be readable and moderately emphasized without oversized, heavy text.
- Avoid excessive nested cards, borders, repeated labels such as “Change,” and unnecessary decorative elements.
- Make interactive rows visibly actionable while keeping their layout calm. Show selected values clearly rather than wasting space.
- Prefer compact two-action dialogs with clear wording. Avoid oversized decorative icons.
- Preserve touch target size, accessibility, and text contrast even when visible icons are small or delicate.

Approved screenshots are the visual reference for requested implementation work. Preserve their spacing, alignment, color relationships, and interaction intent. Do not claim an exact match when the reference is unavailable.

## Typography and localization

Support Persian RTL and English LTR throughout the app. Use start/end alignment so layouts mirror correctly; place leading icons at the start of the current reading direction.

Use localized resources for user-facing text. In Persian, use **فعالیت** for the task concept. Handle long text, locale-appropriate dates, and mixed-direction time strings deliberately.

Use Material typography roles through the project's text/theme abstractions. The preferred Persian font direction is **Vazirmatn**; verify the installed fonts before assuming it is already configured. Apply fonts centrally through typography rather than individually across screens.

## Feature interaction conventions

### Forms

- Use a consistent form header with a feature-colored text save action.
- Provide clear field labels and comfortable space between labels and fields. Avoid redundant placeholder hints.
- Align a multiline description icon with its first line rather than vertically centering it.
- Use consistent reusable selection rows for groups, reminders, and images. Keep icon and title aligned and show the current value opposite them where space allows.
- Image selection may support several images; the form can display a count without expanding into a large preview gallery.

### Lists

- Distinguish items using restrained surfaces, borders, and spacing.
- Keep task titles and completion controls prominent. Show a neutral group indicator and reminder information when applicable.
- Communicate priority separately, such as a narrow edge accent. Do not use priority color as group color.
- Keep list previews concise; the agreed task-list direction omits description text.
- Search and filters should have clear active and inactive states. Reset restores an unfiltered state.
- Completion filters represent completed and incomplete tasks; no selected status means no status restriction.

### Groups and pickers

- Keep selection, quick creation, and management clearly distinguishable.
- Group selection should not repeat a folder icon beside every group.
- Allow quick group creation inline within the picker. Confirming creation selects the new group; cancelling creation returns to the normal picker state.
- Make group management clearly clickable and accessible from the selection flow and settings where appropriate.
- Date and time controls should be reusable across features. Reminder editing may combine date, scrolling time selection, and repetition in one coherent surface.
- Keep recurrence logic in the domain/platform scheduling layers, not in the date picker. A recurring reminder does not require date-range selection.

## Engineering and collaboration

Lilo is both a real product and a project for developing strong engineering skills. Favor maintainable, idiomatic Kotlin and explain substantial architectural choices briefly.

Inspect the relevant implementation before changing it. Improve existing abstractions when useful, preserve working behavior, and avoid unrelated rewrites. Treat planned features as plans, not as already implemented capabilities.

Add meaningful tests for business rules, state transitions, mappings, and scheduling behavior when changing those areas. Use previews for reusable and feature-specific UI components, including representative empty, selected, and populated states. Consider both languages, themes, and platforms.

Do not introduce data loss, weaken credential security, replace the brand asset, or change established feature colors without a clear reason and explicit agreement. Database evolution should reflect the actual release and user-data situation; an early development decision to skip a migration is not a permanent policy.

Keep responses concise. State what changed, what was checked, and any meaningful limitation. Ask questions when a material product decision is unclear; handle routine implementation choices independently.
