package com.sepideh.lilo.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

/*
 * Material color semantics in Lilo:
 *
 * primary
 * → Global Lilo brand accent.
 * → Example: brand buttons, active brand elements, progress indicators.
 *
 * onPrimary
 * → Content displayed on top of primary.
 * → Example: text/icon inside a primary-colored button.
 *
 * background
 * → Main app/page background.
 * → Example: background behind Home, Task List, Add Task.
 *
 * onBackground
 * → Content directly displayed on the app background.
 * → Example: screen title, back icon, main content placed directly on background.
 *
 * surface
 * → Main component surfaces.
 * → Example: text fields, cards, dialogs.
 *
 * onSurface
 * → High-emphasis content displayed on a surface.
 * → Example: user-entered text in a text field,for example task title.
 *
 * surfaceVariant
 * → Alternative/subtle surface used to visually separate content.
 * → Example: filter/group containers and inactive UI areas.
 *
 * onSurfaceVariant
 * → Secondary content displayed on surfaces.
 * → Example: text-field labels, hints, subtitles, metadata and supporting text.
 *
 * surfaceContainer
 * → Container surface with more visual separation from the background.
 * → Example: cards, sheets and grouped sections.
 *
 * surfaceContainerLow
 * → Subtle container close to the page background.
 * → Example: low-emphasis cards or lightly separated sections.
 *
 * outline
 * → Stronger component borders.
 * → Example: emphasized outlined controls.
 *
 * outlineVariant
 * → Low-emphasis borders and separators.
 * → Example: unfocused text-field borders, dividers and unselected chips.
 *
 * error
 * → Validation/error state.
 * → Example: invalid field border and validation message.
 *
 *
 * Feature identity is intentionally NOT represented by Material primary:
 *
 * LiloExtendedTheme.colors.taskColor     → Tasks
 * LiloExtendedTheme.colors.noteColor     → Notes
 * LiloExtendedTheme.colors.expenseColor  → Expenses
 * LiloExtendedTheme.colors.passwordColor → Passwords
 *
 * Material primary = Lilo identity.
 * Feature colors = feature identity.
 */

val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFFF5722),
    onPrimary = LiloColorsLight.onAccent,

    background = LightBackground,
    onBackground = LiloColorsLight.textPrimary,

    surface = White,
    onSurface = LiloColorsLight.textPrimary,

    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LiloColorsLight.textSecondary,

    surfaceContainer = White,
    surfaceContainerLow = LightSurfaceContainerLow,

    outline = LiloColorsLight.textSupporting,
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFDEE2E8),

    scrim = LiloColorsLight.title,
    onSecondary = LiloColorsLight.onAccent,
    onTertiary = LiloColorsLight.onAccent,
    error = LightError,
)

val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFFFAB91),
    onPrimary = LiloColorsLight.onAccent,

    background = DarkBackground,
    onBackground = LiloColorsDark.textPrimary,

    surface = Gray900,
    onSurface = LiloColorsDark.textPrimary,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LiloColorsDark.textSecondary,

    surfaceContainer = BlueGray900,
    surfaceContainerLow = DarkSurfaceContainerLow,

    outline = Gray400,
    outlineVariant = Gray800,

    scrim = LiloColorsDark.onAccent,
    onSecondary = LiloColorsDark.onAccent,
    onTertiary = LiloColorsDark.onAccent,
    error = DarkError,
)