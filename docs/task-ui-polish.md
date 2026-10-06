# Task UI polish

The approved Task List and Add/Edit Task screenshots remain the composition reference: warm background, white rounded surfaces, navy/slate typography, amber actions, neutral group chips, priority edge strips, compact image counts, and integrated text-save headers. Completion, filtering, form drafts, reminders, native image import, and persistence retain the existing state/action boundaries.

## Shared design system

`ui/theme` owns semantic title, primary, secondary, supporting, placeholder and disabled colors, accent foregrounds, priority colors, typography, spacing, sizes and shapes. Enabled light-theme text roles meet 4.5:1 contrast on white and the app background. Amber remains the Tasks identity; a darker amber text-action role provides readable small save/filter labels. Visible pure-black Kotlin UI colors and the black Android night splash surface were replaced with semantic soft neutrals.

Vazirmatn regular and medium are bundled Compose font resources for Persian and English. The central Material typography uses restrained regular/medium weights and explicit line heights. Font assets are from the official Vazirmatn v33.003 release; the SIL Open Font License is retained in `licenses/Vazirmatn-OFL.txt`. No runtime font download or new library is required.

Fifteen project-owned vector resources use a 24-unit grid, 1.6-unit rounded strokes, consistent density and theme tinting. Shared access is in `core/presentation/icons`. Navigation, search, sliders, fields, groups, reminders, images, add, delete, close and checks use this system. Directional assets mirror in RTL.

## Components and supporting flows

Feature-independent chips, checkboxes, fields, form selection rows, group selection/management, inline creation, headers and confirmation surfaces live in `core/presentation`. Task cards, Task priority controls and Task reminder formatting remain feature-owned. The selected priority check is white inside its colored dot; completion uses the screenshot's softer amber box with navy check.

Manage Groups now uses the same rounded sheet, header, spacing and row vocabulary as group selection. It retains deletion confirmation and adds access to the existing quick-create action. The reusable inline editor requests focus, supports IME Done, handles busy/blank states, and clears input/focus on cancel or successful creation. Lists reserve space for footer controls when the keyboard appears. Persistence and validation remain caller-owned.

Related search/filter, reminder/repetition, empty states, permission/deletion dialogs, selection and disabled states share the semantic hierarchy and vectors. Persian time and image counts use a shared digit formatter. Selection rows stack values on narrow layouts or enlarged fonts. Native image selection still lives in core: Android Photo Picker through existing Activity Result APIs and iOS PHPicker, with common contracts and import behavior.

## Decisions beyond the screenshots

- Group management uses a sheet aligned with its picker instead of a visually separate generic dialog.
- Quick creation reuses existing behavior; no speculative rename workflow was introduced.
- Subtle selected borders/tints and muted neutral delete affordances keep amber deliberate.
- Small text actions use accessible darker amber while filled actions retain the feature accent.
- Supporting empty states use a restrained icon and copy; form values adapt for narrow widths and font scaling.

## Validation

Android compilation and all 20 host tests pass, including two semantic-color/contrast tests. The shared theme, fonts, vectors and reusable polished components compile for iOS Simulator ARM64 in an isolated check using the repository's cached versions. This is not a complete iOS application build: the existing Persian date-picker dependency cycle still blocks that build. Diff whitespace checks pass.

Device-level visual QA was not completed because emulator launch was declined. Persian/English and light/dark preview coverage is present; pixel-exact and runtime keyboard/layout behavior still require device inspection.
